package org.itech.framework.javafxapp.demo.controllers.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.itech_framework.core.annotations.methods.PreDestroy;
import io.github.itech_framework.java_fx.loader.FxComponentLoader;
import io.github.itech_framework.java_fx.ui.dialog.ModalDialog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import io.github.itech_framework.core.annotations.methods.OnInit;
import io.github.itech_framework.core.annotations.properties.Property;
import io.github.itech_framework.core.annotations.reactives.Rx;
import io.github.itech_framework.core.annotations.storage.DataStorage;
import io.github.itech_framework.core.utils.DataStorageUtil;
import io.github.itech_framework.java_fx.annotations.FxController;
import io.github.itech_framework.java_fx.router.Router;
import io.github.itech_framework.java_fx.router.core.Routable;
import javafx.scene.input.MouseEvent;
import org.itech.framework.javafxapp.demo.TaskManagerApplication;
import org.itech.framework.javafxapp.demo.utils.components.WelcomeWidget;

import java.util.Objects;

@FxController
public class DashboardController implements Routable {

    @FXML
    public TextField searchInput;
    @FXML
    public Label title;

    @FXML
    private Button darkModeBtn;

    @Property(key = "flexi.app.name")
    private String appName;

    @Rx
    Router router;

    @DataStorage(key = "isDarkMode")
    private boolean isDarkMode;

    @DataStorage(key = "doNotShowWelcomeDialog")
    private boolean doNotShowWelcomeDialog;

    @DataStorage(key = "isAlreadyShowWelcomeDialog")
    private boolean isAlreadyShowWelcomeDialog;

    @OnInit
    public void initializeData(){
        title.setText(appName);
        Platform.runLater(()->{
            darkModeBtn.setText(isDarkMode ? "Light Mode" : "Dark Mode");
            darkModeBtn.setOnAction((e) -> {
                isDarkMode = !isDarkMode;
                DataStorageUtil.save("isDarkMode", isDarkMode);
                router.refresh();
            });
            if(!doNotShowWelcomeDialog && !isAlreadyShowWelcomeDialog){
                WelcomeWidget welcomeWidget = FxComponentLoader.load(WelcomeWidget.class, "Welcome to " + appName);
                DataStorageUtil.save("isAlreadyShowWelcomeDialog", true);
                ModalDialog.builder().addOwner(TaskManagerApplication.ownerStage)
                        .customFonts("Poppins")
                        .addCloseButton("Close")
                        .addActionButton("Do Not Show Again", ()->{
                            DataStorageUtil.save("doNotShowWelcomeDialog", true);
                        })
                        .addContent(welcomeWidget.getRoot())
                        .build().show();

            }
        });
    }

    @FXML
    public void handleCardClick(MouseEvent event) {
        AnchorPane clickedCard = (AnchorPane) event.getSource();
        String styleClass = clickedCard.getStyleClass().toString();

        ObjectMapper mapper = new ObjectMapper();

        try {
            System.out.println(mapper.writeValueAsString(router));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (styleClass.contains("today")) {
            Platform.runLater(()->{
                router.to("tasks");
            });
        } else if (styleClass.contains("fav")) {
            System.out.println("Fav Page Redirect.");
        } else if (styleClass.contains("done")) {
            System.out.println("Completed Page Redirect.");
        } else if (styleClass.contains("note")) {
            System.out.println("Note Page redirect");
        }
    }

    @PreDestroy
    protected void beforeDestroyTheController(){
        DataStorageUtil.save("isAlreadyShowWelcomeDialog", false);
    }
}
