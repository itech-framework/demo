package org.itech.framework.javafxapp.demo.controllers.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.itech.framework.fx.core.annotations.methods.InitMethod;
import org.itech.framework.fx.core.annotations.reactives.Rx;
import org.itech.framework.fx.core.annotations.storage.DataStorage;
import org.itech.framework.fx.core.utils.DataStorageUtil;
import org.itech.framework.fx.core.utils.validator.CommonValidator;
import org.itech.framework.fx.java_fx.annotations.FxController;
import org.itech.framework.fx.java_fx.input.validations.FormValidator;
import org.itech.framework.fx.java_fx.input.validations.validator.ValidationResult;
import org.itech.framework.fx.java_fx.router.Router;
import org.itech.framework.fx.java_fx.router.core.Routable;
import org.itech.framework.fx.java_fx.ui.dialog.AlertDialog;
import org.itech.framework.javafxapp.demo.HelloApplication;
import org.itech.framework.javafxapp.demo.common.date_time.DateTimeConstant;
import org.itech.framework.javafxapp.demo.common.date_time.DateTimeUtil;
import org.itech.framework.javafxapp.demo.common.enums.EnumObject;
import org.itech.framework.javafxapp.demo.common.enums.PriorityStatus;
import org.itech.framework.javafxapp.demo.common.enums.TaskStatus;
import org.itech.framework.javafxapp.demo.dtos.TaskDTO;
import org.itech.framework.javafxapp.demo.services.TaskService;

import java.time.format.DateTimeFormatter;

@FxController
public class TaskViewController implements Routable {

    @FXML
    private Button darkModeBtn;
    @FXML
    private ComboBox<EnumObject> priorityCombobox;
    @FXML
    private TableView<TaskDTO> taskTable;
    @FXML
    private TextField taskTitleText;
    @FXML
    private TextArea description;
    @FXML
    private DatePicker startDateText;
    @FXML
    private DatePicker dueDateText;

    @FXML
    private TableColumn<TaskDTO, Integer> numberColumn;
    @FXML
    private TableColumn<TaskDTO, String> titleColumn;
    @FXML
    private TableColumn<TaskDTO, String> startDateColumn;
    @FXML
    private TableColumn<TaskDTO, String> dueDateColumn;
    @FXML
    private TableColumn<TaskDTO, Integer> progressColumn;
    @FXML
    private TableColumn<TaskDTO, String> statusColumn;
    @FXML
    private TableColumn<TaskDTO, String> priorityColumn;

    @Rx
    Router router;

    @Rx
    TaskService taskService;

    @DataStorage(key = "isDarkMode")
    private boolean isDarkMode;

    private final FormValidator createTaskValidator = new FormValidator();

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
        router.back();
    }

    @InitMethod
    public void init() {
        Platform.runLater(() -> {
            darkModeBtn.setText(isDarkMode ? "Light Mode" : "Dark Mode");
            darkModeBtn.setOnAction((e) -> {
                isDarkMode = !isDarkMode;
                DataStorageUtil.save("isDarkMode", isDarkMode);
                router.refresh();
            });
            priorityCombobox.getItems().addAll(PriorityStatus.getAll());
            configureTableColumns();
            setupTableContextMenu();
            taskTable.getItems().addAll(taskService.getAllTask());
            applyFormValidator();
        });

    }

    private void applyFormValidator() {
        createTaskValidator.addRequiredField(taskTitleText)
                .addRequiredField(description)
                .addRule(
                        startDateText,
                        value ->
                                new ValidationResult(CommonValidator.isValidObject(value), "Start date cannot be empty!"), "Start date cannot be empty!")
                .addRule(
                        dueDateText,
                        value ->
                                new ValidationResult(CommonValidator.isValidObject(value), "Due date cannot be empty!"), "Due date cannot be empty!")
                .addRule(
                        priorityCombobox,
                        value ->
                                new ValidationResult(CommonValidator.isValidObject(value), "Priority cannot be empty!"), "Priority cannot be empty!");

    }

    // In your controller's init method
    private void configureTableColumns() {
        numberColumn.setCellFactory(col -> new TableCell<TaskDTO, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        // Title column
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        // Start Date
        startDateColumn.setCellValueFactory(cellData ->
                cellData.getValue().startDateDescProperty());

        // Due Date
        dueDateColumn.setCellValueFactory(cellData ->
                cellData.getValue().dueDateDescProperty());

        // Progress (with progress bar)
        // In your controller's initialization method
        progressColumn.setCellValueFactory(cellData ->
                cellData.getValue().progressProperty().asObject()
        );

        progressColumn.setCellFactory(column -> new TableCell<>() {
            private final ProgressBar progressBar = new ProgressBar();

            {
                progressBar.setMaxWidth(Double.MAX_VALUE);
            }

            @Override
            protected void updateItem(Integer progress, boolean empty) {
                super.updateItem(progress, empty);

                if (empty || progress == null) {
                    setGraphic(null);
                } else {
                    // Convert 0-100 to 0.0-1.0
                    double ratio = progress / 100.0;
                    progressBar.setProgress(ratio);
                    setGraphic(progressBar);
                }
            }
        });

        // Status
        statusColumn.setCellValueFactory(cellData ->
                cellData.getValue().statusDescProperty());

        // Priority
        priorityColumn.setCellValueFactory(cellData ->
                cellData.getValue().priorityDescProperty());
    }

    private void setupTableContextMenu() {
        // Create context menu
        ContextMenu contextMenu = new ContextMenu();

        MenuItem viewItem = new MenuItem("View");
        MenuItem editItem = new MenuItem("Edit");
        MenuItem updateProgressItem = new MenuItem("Update Progress");
        MenuItem deleteItem = new MenuItem("Delete");

        // Add icons (requires font awesome or other icon library)
        viewItem.setGraphic(new Label("\uf06e")); // FontAwesome icon
        editItem.setGraphic(new Label("\uf044"));
        updateProgressItem.setGraphic(new Label("\uf251"));
        deleteItem.setGraphic(new Label("\uf1f8"));

        contextMenu.getItems().addAll(viewItem, editItem, updateProgressItem, deleteItem);

        // Set row factory
        taskTable.setRowFactory(tv -> {
            TableRow<TaskDTO> row = new TableRow<>();

            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    // Select the row first
                    taskTable.getSelectionModel().select(row.getIndex());
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });

            return row;
        });

        // Menu actions
        viewItem.setOnAction(event -> viewSelectedTask());
        editItem.setOnAction(event -> editSelectedTask());
        updateProgressItem.setOnAction(event -> updateTaskProgress());
        deleteItem.setOnAction(event -> deleteSelectedTask());
    }

    private void viewSelectedTask() {
        TaskDTO selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Implement view logic
            System.out.println("Viewing: " + selected.getTitle());
        }
    }

    private void editSelectedTask() {
        TaskDTO selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Implement edit logic
            System.out.println("Editing: " + selected.getTitle());
        }
    }

    private void updateTaskProgress() {
        TaskDTO selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Implement progress update logic
            System.out.println("Updating progress for: " + selected.getTitle());
        }
    }

    private void deleteSelectedTask() {
        TaskDTO selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Create confirmation alert
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Task");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete '" + selected.getTitle() + "'?");

            // Apply dark mode if needed
            if (isDarkMode) {
                alert.getDialogPane().getStyleClass().add("dark-mode");
            }

            // Add application styles
            alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("/static/css/style.css").toExternalForm()
            );

            // Customize buttons
            ButtonType deleteButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(deleteButton, cancelButton);

            // Show and wait for response
            alert.showAndWait().ifPresent(response -> {
                if (response == deleteButton) {
                    taskTable.getItems().remove(selected);
                    // Add database deletion logic here if needed
                    System.out.println("Deleted task: " + selected.getTitle());
                }
            });
        }
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
    private void handleCreateTask(ActionEvent actionEvent) {
        if (createTaskValidator.validate()) {

            TaskDTO task = new TaskDTO();
            task.setTitle(taskTitleText.getText());
            task.setStatus(TaskStatus.PROGRESS.getCode());
            task.setPriority(priorityCombobox.getValue().code());
            task.setDueDate(DateTimeUtil.convertToDate(dueDateText.getValue()));
            task.setStartDate(DateTimeUtil.convertToDate(startDateText.getValue()));
            task.setDescription(description.getText());
            task.setProgress(0);

            try {
                TaskDTO saved = this.taskService.manageTask(task);
                if(CommonValidator.isValidObject(saved)){
                    Platform.runLater(()->{
                        taskTable.getItems().add(saved);
                    });
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.show();
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Create Task success.");
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please filled all required fields!");
            alert.show();
        }
    }
}
