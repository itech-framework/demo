package org.itech.framework.javafxapp.demo.utils.google;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

public class GoogleLoginHandler {
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private String codeVerifier;
    private HttpServer server;
    @Setter
    private LoginCallback callback;

    @FunctionalInterface
    public interface LoginCallback {
        void onResponse(UserInfo response, Exception e);
    }

    @Getter
    public static class UserInfo {
        private final String email;
        private final String name;
        private final String pictureUrl;
        private final String accessToken;
        private final String refreshToken;
        private final Long expiresIn;

        public UserInfo(String email, String name, String pictureUrl,
                        String accessToken, String refreshToken, Long expiresIn) {
            this.email = email;
            this.name = name;
            this.pictureUrl = pictureUrl;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiresIn = expiresIn;
        }
    }

    public GoogleLoginHandler(String clientId, String clientSecret, String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    public void startLogin() {
        new Thread(() -> {
            try {
                codeVerifier = generateCodeVerifier();
                String codeChallenge = generateCodeChallenge(codeVerifier);

                GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                        new NetHttpTransport(),
                        new GsonFactory(),
                        clientId,
                        clientSecret,
                        Collections.singleton("openid email profile"))
                        .setDataStoreFactory(new MemoryDataStoreFactory())
                        .build();

                GoogleAuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl()
                        .setRedirectUri(redirectUri)
                        .set("code_challenge", codeChallenge)
                        .set("code_challenge_method", "S256")
                        .set("access_type", "offline")
                        .set("prompt", "consent");

                openBrowser(authorizationUrl.build());
                startCallbackServer();
            } catch (Exception e) {
                if (callback != null) {
                    callback.onResponse(null, e);
                }
            }
        }).start();
    }

    private void startCallbackServer() throws IOException {
        URI uri = URI.create(redirectUri);
        int port = uri.getPort() > 0 ? uri.getPort() :
                uri.getScheme().equals("https") ? 443 : 80;

        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(uri.getPath(), exchange -> {
            try {
                Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
                String code = params.get("code");

                GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        new GsonFactory(),
                        clientId,
                        clientSecret,
                        code,
                        redirectUri)
                        .set("code_verifier", codeVerifier)
                        .execute();

                // Validate ID token
                GoogleIdToken idToken = tokenResponse.parseIdToken();
                GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                        new NetHttpTransport(),
                        new GsonFactory())
                        .setAudience(Collections.singleton(clientId))
                        .build();

                if (!verifier.verify(idToken)) {
                    throw new SecurityException("Invalid ID token signature");
                }

                UserInfo userInfo = getUserInfo(idToken, tokenResponse);

                if (callback != null) {
                    callback.onResponse(userInfo, null);
                }

                sendResponse(exchange, 200, "Login successful! You can close this window.");
            } catch (Exception e) {
                if (callback != null) {
                    callback.onResponse(null, e);
                }
                sendResponse(exchange, 400, "Error: " + e.getMessage());
            } finally {
                exchange.close();
                server.stop(0);
            }
        });
        server.start();
    }

    private static UserInfo getUserInfo(GoogleIdToken idToken, GoogleTokenResponse tokenResponse) throws Exception {
        GoogleIdToken.Payload payload = idToken.getPayload();

        String accessToken = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken();
        if (refreshToken == null) {
            throw new Exception("Refresh token missing - user consent may be required");
        }

        UserInfo userInfo = new UserInfo(
                payload.getEmail(),
                (String) payload.get("name"),
                (String) payload.get("picture"),
                accessToken,
                refreshToken,
                tokenResponse.getExpiresInSeconds());
        return userInfo;
    }

    // Helper methods
    private String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifier = new byte[32];
        secureRandom.nextBytes(codeVerifier);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }

    private String generateCodeChallenge(String codeVerifier) {
        try {
            byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate code challenge", e);
        }
    }

    private void openBrowser(String url) throws IOException {
        java.awt.Desktop.getDesktop().browse(URI.create(url));
    }

    private Map<String, String> parseQuery(String query) {
        return Arrays.stream(query.split("&"))
                .map(pair -> pair.split("="))
                .collect(Collectors.toMap(
                        pair -> URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                        pair -> URLDecoder.decode(pair[1], StandardCharsets.UTF_8)));
    }

    private void sendResponse(com.sun.net.httpserver.HttpExchange exchange, int code, String message) throws IOException {
        exchange.sendResponseHeaders(code, message.length());
        try (var os = exchange.getResponseBody()) {
            os.write(message.getBytes());
        }
    }

    public static GoogleTokenResponse refreshAccessToken(String clientId, String clientSecret,
                                                         String refreshToken) throws IOException {
        return new GoogleRefreshTokenRequest(
                new NetHttpTransport(),
                new GsonFactory(),
                refreshToken,
                clientId,
                clientSecret)
                .execute();
    }
}