package org.itech.framework.javafxapp.demo;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.itech.framework.fx.core.annotations.ComponentScan;
import org.itech.framework.fx.core.annotations.api_client.EnableApiClient;
import org.itech.framework.fx.core.annotations.jfx.EnableJavaFx;
import org.itech.framework.fx.core.annotations.storage.DataStorage;
import org.itech.framework.fx.java_fx.ITechJavaFxApplication;
import org.itech.framework.javafxapp.demo.controllers.dashboard.DashboardController;
import org.itech.framework.javafxapp.demo.controllers.tasks.MyTaskViewController;

import java.util.Objects;

@ComponentScan(basePackage = "org.itech.framework.javafxapp.demo")
@EnableJavaFx
@EnableApiClient
public class HelloApplication extends ITechJavaFxApplication {

    @DataStorage
    private Boolean authenticated;

    @Override
    public void onInit() throws Exception {
        router.getConfig().addTransition("fade", root -> {
            FadeTransition ft = new FadeTransition(Duration.millis(300), root);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        });

        router.getConfig().addTransition("upToDown", root -> {
            root.setOpacity(0);
            root.setTranslateY(-20);

            ParallelTransition pt = new ParallelTransition();

            FadeTransition ft = new FadeTransition(Duration.millis(300), root);
            ft.setFromValue(0);
            ft.setToValue(1);

            TranslateTransition tt = new TranslateTransition(Duration.millis(300), root);
            tt.setFromY(-20);
            tt.setToY(0);

            pt.getChildren().addAll(ft, tt);
            pt.play();
        });

        router.getConfig().addStyleSheets(Objects.requireNonNull(getClass().getResource("/static/css/app.css")).toExternalForm());

        router.registerRoute("dashboard", "dashboard/dashboard-view.fxml", DashboardController.class, "fade");
        router.registerRoute("today-tasks", "tasks/today-tasks-view.fxml", MyTaskViewController.class, "upToDown");
    }

    @Override
    public void start(Stage stage) throws Exception {
        router.initialize(HelloApplication.class, stage);
        router.to("dashboard");

        stage.setTitle("Task Management System");
        stage.setWidth(600);
        stage.setHeight(500);
        Platform.runLater(()->{
            stage.getScene().getRoot().setFocusTraversable(true);
            stage.getScene().getRoot().requestFocus();
        });
        stage.show();
    }
    public static void main(String[] args) throws Exception {
        ITechJavaFxApplication.run(HelloApplication.class,args);
    }
}