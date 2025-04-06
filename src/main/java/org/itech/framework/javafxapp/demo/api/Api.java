package org.itech.framework.javafxapp.demo.api;

import org.itech.framework.fx.api_client.annotations.ApiClient;
import org.itech.framework.fx.api_client.annotations.methods.POST;
import org.itech.framework.fx.api_client.annotations.parameters.Body;
import org.itech.framework.javafxapp.demo.api.requests.LoginRequest;
import org.itech.framework.javafxapp.demo.api.responses.BasedResponse;

import java.util.concurrent.CompletableFuture;

@ApiClient
public interface Api {
    @POST("/auth/login")
    BasedResponse<?> login(@Body LoginRequest request);

    @POST("/auth/login")
    CompletableFuture<BasedResponse<?>> loginAsync(@Body LoginRequest request);
}
