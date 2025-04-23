package org.itech.framework.javafxapp.demo.controllers.tasks;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import io.github.itech_framework.core.annotations.methods.OnInit;
import io.github.itech_framework.core.annotations.properties.Property;
import io.github.itech_framework.core.annotations.storage.DataStorage;
import io.github.itech_framework.core.utils.DataStorageUtil;
import io.github.itech_framework.core.utils.validator.CommonValidator;
import io.github.itech_framework.java_fx.annotations.FxController;
import io.github.itech_framework.java_fx.ui.dialog.AlertDialog;
import io.github.itech_framework.java_fx.utils.concurrent.BackgroundTaskService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itech.framework.javafxapp.demo.TaskManagerApplication;
import org.itech.framework.javafxapp.demo.utils.google.GoogleLoginHandler;

import java.io.IOException;

@FxController
public class TeamViewController {

    private final Logger logger = LogManager.getLogger(getClass());

    @FXML
    public Label loadingText;
    @FXML
    private StackPane loadingOverlay;
    @FXML
    private ProgressIndicator loadingSpinner;

    @DataStorage(key = "accessToken")
    private String accessToken;

    @DataStorage(key = "refreshToken")
    private String refreshToken;

    @DataStorage(key = "expiresAt")
    private long expiresAt;

    @Property(key = "google.client.id")
    private String clientId;
    @Property(key = "google.client.secret")
    private String clientSecret;
    @Property(key = "google.auth.redirect.url")
    private String callbackUrl;

    @OnInit
    private void onInit(){
        Platform.runLater(()->{
            if(!isValidToken()){
                handleTokenRefreshOrLogin();
            }else{
                logger.info("User already login!");
                AlertDialog.builder()
                        .level(AlertDialog.Level.INFO)
                        .addOwner(TaskManagerApplication.ownerStage)
                        .title("Notification")
                        .message("User already login.")
                        .build().show();
                loadingOverlay.setVisible(false);
                loadingOverlay.setManaged(false);
            }
        });
    }

    private void handleTokenRefreshOrLogin() {
        GoogleLoginHandler loginHandler = getGoogleLoginHandler();

        if(CommonValidator.validString(refreshToken)){
            loadingOverlay.setVisible(true);
            loadingOverlay.setManaged(true);
            loadingText.setText("Refreshing token...");
            BackgroundTaskService.getInstance().executeTask(
                    ()-> GoogleLoginHandler.refreshAccessToken(clientId,clientSecret, refreshToken),
                    (data)->{
                        loadingOverlay.setVisible(false);
                        loadingOverlay.setManaged(false);
                        AlertDialog.builder()
                                .level(AlertDialog.Level.SUCCESS)
                                .addOwner(TaskManagerApplication.ownerStage)
                                .title("Success!")
                                .message("Token has been refresh.")

                                .build().show();

                        DataStorageUtil.save("accessToken", data.getAccessToken());
                        DataStorageUtil.save("refreshToken", data.getRefreshToken());
                        DataStorageUtil.save("expiresAt", System.currentTimeMillis() + (data.getExpiresInSeconds() * 1000));
                    },
                    (error)->{
                        logger.error("Error on refresh token: {}", ExceptionUtils.getStackTrace(error));
                        loadingOverlay.setVisible(false);
                        loadingOverlay.setManaged(false);
                        AlertDialog.builder()
                                .addOwner(TaskManagerApplication.ownerStage)
                                .level(AlertDialog.Level.ERROR)
                                .title("Failed to refresh token! Please try to login again.")
                                .addButton("Login", "primary", ()->{
                                    DataStorageUtil.save("refreshToken", "");
                                    loginWithGoogle(loginHandler);
                                })
                                .message(error.getMessage())
                                .build().show();
                    }
            );
        }else{
            loginWithGoogle(loginHandler);
        }
    }

    private void loginWithGoogle(GoogleLoginHandler loginHandler){
        AlertDialog.builder()
                .level(AlertDialog.Level.INFO)
                .addOwner(TaskManagerApplication.ownerStage)
                .title("Unauthorized")
                .message("Do you want to continue with Google?")
                .addButton("Close", ()->{})
                .addButton("Open Browser", "primary", ()->{
                    loadingOverlay.setVisible(true);
                    loadingOverlay.setManaged(true);
                    loadingText.setText("Authenticating...");
                    loginHandler.startLogin();
                }).build().show();
    }

    private GoogleLoginHandler getGoogleLoginHandler() {
        GoogleLoginHandler loginHandler = new GoogleLoginHandler(clientId, clientSecret, callbackUrl);

        loginHandler.setCallback((userInfo,e)->{
            Platform.runLater(()->{
                loadingOverlay.setVisible(false);
                loadingOverlay.setManaged(false);

                if(e != null){
                    AlertDialog.builder()
                            .level(AlertDialog.Level.ERROR)
                            .addOwner(TaskManagerApplication.ownerStage)
                            .title("Failed to login with google!")
                            .message(e.getMessage())
                            .build().show();
                }else{
                    AlertDialog.builder()
                            .level(AlertDialog.Level.SUCCESS)
                            .addOwner(TaskManagerApplication.ownerStage)
                            .title("Success!")
                            .message("Login with google successfully!")
                            .build().show();
                    DataStorageUtil.save("accessToken", userInfo.getAccessToken());
                    DataStorageUtil.save("refreshToken", userInfo.getRefreshToken());
                    DataStorageUtil.save("expiresAt", System.currentTimeMillis() + (userInfo.getExpiresIn() * 1000));
                }
            });
        });
        return loginHandler;
    }

    private boolean isValidToken(){
        return CommonValidator.validString(accessToken) && System.currentTimeMillis() < expiresAt;
    }

}
