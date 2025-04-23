package org.itech.framework.javafxapp.demo.controllers.tasks;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import io.github.itech_framework.core.annotations.methods.OnInit;
import io.github.itech_framework.core.annotations.reactives.Rx;
import io.github.itech_framework.java_fx.annotations.FxController;
import io.github.itech_framework.java_fx.loader.FxControllerLoader;
import io.github.itech_framework.java_fx.router.Router;
import io.github.itech_framework.java_fx.router.core.Routable;
import io.github.itech_framework.java_fx.ui.dialog.AlertDialog;
import io.github.itech_framework.java_fx.utils.concurrent.BackgroundTaskService;
import org.itech.framework.javafxapp.demo.TaskManagerApplication;

@FxController
public class TaskViewController implements Routable {

    @FXML
    private HBox tagButtonContainer;
    @FXML
    private ProgressIndicator loadingSpinner;
    @FXML
    private StackPane loadingOverlay;
    @FXML
    private AnchorPane contentContainer;

    @Rx
    Router router;

    @OnInit
    public void onInit(){
        Platform.runLater(()->{
            handlePageChange(ListViewController.class, "/views/tasks/list-view.fxml");
        });
    }

    @Override
    public void onNavigate(Object arguments) {
        Routable.super.onNavigate(arguments);
        System.out.println("arguments: " + arguments);
    }

    @Override
    public void onReturn(Object result) {
        Routable.super.onReturn(result);
        System.out.println("return: " + result);
    }

    public void handleBackBtnClicked(ActionEvent actionEvent) {
        router.pop();
    }

    @Override
    public void postRefresh() {
        System.out.println("Post refresh...");
    }

    @Override
    public void preRefresh() {
        System.out.println("Pre refresh...");
    }

    @FXML
    public void handleTagChanged(ActionEvent e) {
        if(e.getTarget() instanceof Button b){

            tagButtonContainer.getChildren().forEach((node)->{
                if(node instanceof Button btn){
                    btn.getStyleClass().remove("active");
                }
            });

            if(b.getStyleClass().contains("tag-btn")){
                if(b.getStyleClass().contains("list")){
                    Platform.runLater(()->{
                        b.getStyleClass().add("active");
                        handlePageChange(ListViewController.class, "/views/tasks/list-view.fxml");
                    });
                }else
                if(b.getStyleClass().contains("deadline")){
                    Platform.runLater(()->{
                        b.getStyleClass().add("active");
                        handlePageChange(DeadlineViewController.class, "/views/tasks/deadline-view.fxml");
                    });
                }else if(b.getStyleClass().contains("teams")){
                    Platform.runLater(()->{
                        b.getStyleClass().add("active");
                        handlePageChange(TeamViewController.class, "/views/tasks/team-view.fxml");
                    });
                }
            }
        }
    }

    private void pageChange(Node page){
        contentContainer.getChildren().add(page);
    }

    private void handlePageChange(Class<?> clazz, String fxmlPath) {
        // Show loading indicator
        showLoadingIndicator(true);

        BackgroundTaskService.getInstance().executeTask(
                () -> FxControllerLoader.load(clazz, fxmlPath),
                root -> {
                    // Update UI on success (runs on FX thread)
                    pageChange((Node) root);
                    showLoadingIndicator(false);
                },
                ex -> {
                    showLoadingIndicator(false);
                    AlertDialog.builder()
                            .level(AlertDialog.Level.ERROR)
                            .addOwner(TaskManagerApplication.ownerStage)
                            .title("Loading Error")
                            .customFonts("Poppins")
                            .message("Failed to load page:\n" + ex.getMessage())
                            .build()
                            .show();
                },
                this::updateLoadingProgress
        );
    }

    // Example loading indicator methods
    private void showLoadingIndicator(boolean show) {
        Platform.runLater(() -> {
            loadingOverlay.setVisible(show);
            loadingOverlay.setManaged(show);
            loadingSpinner.setVisible(show);
        });
    }

    private void updateLoadingProgress(double progress) {
        Platform.runLater(() -> {
            loadingSpinner.setProgress(progress);
        });
    }
}
