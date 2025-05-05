package org.itech.framework.javafxapp.demo;

import java.io.InputStream;
import java.util.Objects;

import org.itech.framework.javafxapp.demo.controllers.dashboard.DashboardController;
import org.itech.framework.javafxapp.demo.controllers.tasks.TaskViewController;

import io.github.itech_framework.core.annotations.ComponentScan;
import io.github.itech_framework.core.annotations.jfx.EnableJavaFx;
import io.github.itech_framework.core.annotations.persistences.EnableJPA;
import io.github.itech_framework.core.annotations.properties.Property;
import io.github.itech_framework.java_fx.ITechJavaFxApplication;
import io.github.itech_framework.java_fx.router.config.RouterConfig;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

@ComponentScan(basePackage = "org.itech.framework.javafxapp.demo")
@EnableJavaFx
@EnableJPA
public class TaskManagerApplication extends ITechJavaFxApplication {

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

        router.registerRoute("dashboard", "/views/dashboard/dashboard-view.fxml", DashboardController.class, "upToDown");
        router.registerRoute("tasks", "/views/tasks/tasks-view.fxml", TaskViewController.class, "upToDown");
    }

    @Override
    public void start(Stage stage) throws Exception {
        ownerStage = stage;

        router.initialize(TaskManagerApplication.class, stage);
        router.to("dashboard");

        stage.setTitle(appName);
        stage.setWidth(600);
        stage.setHeight(500);
        Platform.runLater(()->{
            stage.getScene().getRoot().setFocusTraversable(true);
            stage.getScene().getRoot().requestFocus();
        });
        stage.setMaximized(true);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/static/images/app-icon.png"))));
        stage.show();
    }
    
    public static void main(String[] args) throws Exception {
        loadFont();
        System.setProperty("prism.lcdtext", "false");
        ITechJavaFxApplication.run(TaskManagerApplication.class,args);
    }

    private static void loadFont() {
        try {
            InputStream fontStream = TaskManagerApplication.class.getClassLoader().getResourceAsStream(
                    "static/fonts/Poppins-Regular.ttf"
            );
            System.out.println(TaskManagerApplication.class.getClassLoader().getResourceAsStream(
                    "static/fonts/Poppins-Regular.ttf"
            ));
            if (fontStream != null) {
                Font font = Font.loadFont(fontStream, 0);
                System.out.println("Loaded font: " + font.getFamily());
            } else {
                System.err.println("Font file not found!");
            }
        } catch (Exception e) {
            System.err.println("Font loading error: " + e.getMessage());
        }
    }

}