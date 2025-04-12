package org.itech.framework.javafxapp.demo;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.itech.framework.fx.core.annotations.ComponentScan;
import org.itech.framework.fx.core.annotations.jfx.EnableJavaFx;
import org.itech.framework.fx.core.annotations.persistences.EnableJPA;
import org.itech.framework.fx.core.annotations.properties.Property;
import org.itech.framework.fx.java_fx.ITechJavaFxApplication;
import org.itech.framework.fx.java_fx.router.config.RouterConfig;
import org.itech.framework.javafxapp.demo.controllers.dashboard.DashboardController;
import org.itech.framework.javafxapp.demo.controllers.tasks.TaskViewController;

import java.util.Objects;

@ComponentScan(basePackage = "org.itech.framework.javafxapp.demo")
@EnableJavaFx
@EnableJPA
public class HelloApplication extends ITechJavaFxApplication {

    @Property(key = "flexi.app.name")
    private String appName;

    public static Stage ownerStage;

    @Override
    public void onInit() throws Exception {
        RouterConfig.setDarkModeKey("isDarkMode");
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

        router.registerRoute("dashboard", "dashboard/dashboard-view.fxml", DashboardController.class, "upToDown");
        router.registerRoute("tasks", "tasks/tasks-view.fxml", TaskViewController.class, "upToDown");
    }

    @Override
    public void start(Stage stage) throws Exception {
        ownerStage = stage;


        router.initialize(HelloApplication.class, stage);
        router.to("dashboard");

        stage.setTitle(appName);
        stage.setWidth(600);
        stage.setHeight(500);
        Platform.runLater(()->{
            stage.getScene().getRoot().setFocusTraversable(true);
            stage.getScene().getRoot().requestFocus();
        });
        stage.setMaximized(true);
        stage.show();
    }
    public static void main(String[] args) throws Exception {
        try {
            Font.loadFont(
                    Objects.requireNonNull(HelloApplication.class.getResource("/static/fonts/FontAwesome5Free-Solid-900.otf")).toExternalForm(),
                    12
            );
        } catch (Exception e) {
            System.err.println("Font loading failed: " + e.getMessage());
        }
        ITechJavaFxApplication.run(HelloApplication.class,args);
    }
}