package org.itech.framework.javafxapp.demo.controllers.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.itech.framework.fx.core.annotations.methods.InitMethod;
import org.itech.framework.fx.core.annotations.reactives.Rx;
import org.itech.framework.fx.core.utils.validator.CommonValidator;
import org.itech.framework.fx.java_fx.annotations.FxController;
import org.itech.framework.fx.java_fx.input.validations.FormValidator;
import org.itech.framework.fx.java_fx.input.validations.validator.ValidationResult;
import org.itech.framework.fx.java_fx.ui.dialog.AlertDialog;
import org.itech.framework.fx.java_fx.utils.SVGUtil;
import org.itech.framework.javafxapp.demo.TaskManagerApplication;
import org.itech.framework.javafxapp.demo.common.date_time.DateTimeUtil;
import org.itech.framework.javafxapp.demo.common.enums.EnumObject;
import org.itech.framework.javafxapp.demo.common.enums.PriorityStatus;
import org.itech.framework.javafxapp.demo.common.enums.TaskStatus;
import org.itech.framework.javafxapp.demo.dtos.TaskDTO;
import org.itech.framework.javafxapp.demo.services.TaskService;

@FxController
public class ListViewController {
    @FXML
    private Button cancelTaskBtn;
    @FXML
    private ComboBox<EnumObject> statusComboBox;
    @FXML
    private Button addTaskBtn;
    @FXML
    private TextField progressInput;

    @FXML
    private ComboBox<EnumObject> priorityComboBox;
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
    TaskService taskService;

    private final FormValidator createTaskValidator = new FormValidator();

    private final BooleanProperty isEditing = new SimpleBooleanProperty(false);

    private TaskDTO selectedTask = null;

    @InitMethod
    public void init() {
        Platform.runLater(() -> {
            priorityComboBox.getItems().addAll(PriorityStatus.getAll());
            statusComboBox.getItems().addAll(TaskStatus.getAll());
            configureTableColumns();
            setupTableContextMenu();
            taskTable.getItems().addAll(taskService.getAllTask());
            applyFormValidator();
            isEditing.addListener((obs, oldVal, newVal) -> {
                addTaskBtn.setText(newVal ? "Update Task":"Add Task");
                // update validation
            });
            progressInput.visibleProperty().bind(isEditing);
            progressInput.managedProperty().bind(isEditing);
            statusComboBox.visibleProperty().bind(isEditing);
            statusComboBox.managedProperty().bind(isEditing);
            cancelTaskBtn.visibleProperty().bind(isEditing);
            cancelTaskBtn.managedProperty().bind(isEditing);
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
                        priorityComboBox,
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
                    double ratio = progress / 100.0;
                    progressBar.setProgress(ratio);
                    setGraphic(progressBar);
                }
            }
        });

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusDesc"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priorityDesc"));
    }

    private void setupTableContextMenu() {
        // Create context menu
        ContextMenu contextMenu = new ContextMenu();

        MenuItem viewItem = new MenuItem("View");
        MenuItem editItem = new MenuItem("Edit");
        /*MenuItem updateProgressItem = new MenuItem("Update Progress");*/
        MenuItem deleteItem = new MenuItem("Delete");

        viewItem.setGraphic(SVGUtil.getIcon(SVGUtil.EYE_ICON_PATH, Color.SKYBLUE,14,14));

        editItem.setGraphic(SVGUtil.getIcon(SVGUtil.EDIT_ICON_PATH, Color.BLUE,14, 14));

        /*updateProgressItem.setGraphic(SVGUtil.getIcon(SVGUtil.SYNC_ICON_PATH, Color.GREEN,14, 14));*/

        deleteItem.setGraphic(SVGUtil.getIcon(SVGUtil.DELETE_ICON_PATH, Color.RED,14,14));

        contextMenu.getItems().addAll(viewItem, editItem, /*updateProgressItem,*/ deleteItem);

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
        /*updateProgressItem.setOnAction(event -> updateTaskProgress());*/
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
        selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            // Implement edit logic
            isEditing.set(true);
            taskTitleText.setText(selectedTask.getTitle());
            description.setText(selectedTask.getDescription());
            startDateText.setValue(DateTimeUtil.convertToLocalDate(selectedTask.getStartDate()));
            dueDateText.setValue(DateTimeUtil.convertToLocalDate(selectedTask.getDueDate()));
            PriorityStatus selectedPriority = PriorityStatus.getByCodeOrThrow(selectedTask.getPriority());

            priorityComboBox.setValue(new EnumObject(selectedPriority.getCode(), selectedPriority.getDesc()));
            progressInput.setText(selectedTask.getProgress() + "");

            TaskStatus selectedTaskStatus = TaskStatus.getByCodeOrThrow(selectedTask.getStatus());
            statusComboBox.setValue(new EnumObject(selectedTaskStatus.getCode(), selectedTaskStatus.getDesc()));
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
            AlertDialog.builder()
                    .level(AlertDialog.Level.WARNING)
                    .addOwner(TaskManagerApplication.ownerStage)
                    .title("Confirm Deletion")
                    .customFonts("Poppins")
                    .message("Are you sure you want to delete '" + selected.getTitle() + "'?")
                    .addButton("Cancel", "disabled", () -> {})  // No action needed for cancel
                    .addButton("Delete", "danger", () -> {

                        try {
                            if(taskService.deleteTask(selected.getId())){
                                taskTable.getItems().remove(selected);
                            }else{
                                AlertDialog.builder()
                                        .level(AlertDialog.Level.ERROR)
                                        .addOwner(TaskManagerApplication.ownerStage)
                                        .title("Delete Operation")
                                        .message("Failed to delete this task!")
                                        .customFonts("Poppins")
                                        .addButton("Ok", ()->{})
                                        .build().show();
                            }
                        } catch (Exception e) {
                            AlertDialog.builder()
                                    .level(AlertDialog.Level.ERROR)
                                    .addOwner(TaskManagerApplication.ownerStage)
                                    .title("Operation Failed")
                                    .customFonts("Poppins")
                                    .message("Error processing task: " + e.getMessage())
                                    .addButton("OK", () -> {})
                                    .build()
                                    .show();
                        }
                        System.out.println("Deleted task: " + selected.getTitle());
                    })
                    .build()
                    .show();
        }
    }

    @FXML
    private void handleCreateTask(ActionEvent actionEvent) {
        if (createTaskValidator.validate()) {
            boolean isUpdateMode = isEditing.get() && selectedTask != null;
            TaskDTO task = isUpdateMode ? selectedTask : new TaskDTO();

            task.setTitle(taskTitleText.getText());
            task.setStatus(isUpdateMode ? statusComboBox.getValue().code() : TaskStatus.PROGRESS.getCode());
            task.setPriority(priorityComboBox.getValue().code());
            task.setDueDate(DateTimeUtil.convertToDate(dueDateText.getValue()));
            task.setStartDate(DateTimeUtil.convertToDate(startDateText.getValue()));
            task.setDescription(description.getText());
            task.setProgress(isUpdateMode ? Integer.parseInt(progressInput.getText()) : 0);

            try {
                TaskDTO saved = this.taskService.manageTask(task);
                if (CommonValidator.isValidObject(saved)) {

                    System.out.println("Saved: " + new ObjectMapper().writeValueAsString(saved));

                    Platform.runLater(() -> {
                        if (isUpdateMode) {
                            int index = taskTable.getItems().indexOf(selectedTask);
                            if (CommonValidator.validInteger(index)) {
                                taskTable.getItems().set(index, saved);
                            }
                        } else {
                            taskTable.getItems().add(saved);
                        }

                        // Success alert
                        AlertDialog.builder()
                                .level(AlertDialog.Level.SUCCESS)
                                .addOwner(TaskManagerApplication.ownerStage)
                                .title("Operation Successful")
                                .customFonts("Poppins")
                                .message((isUpdateMode ? "Update" : "Create") + " Task completed successfully.")
                                .addButton("OK", () -> {})
                                .build()
                                .show();
                    });
                    selectedTask = null;
                    isEditing.set(false);
                }
            } catch (Exception e) {
                // Error alert for exceptions
                AlertDialog.builder()
                        .level(AlertDialog.Level.ERROR)
                        .addOwner(TaskManagerApplication.ownerStage)
                        .title("Operation Failed")
                        .customFonts("Poppins")
                        .message("Error processing task: " + e.getMessage())
                        .addButton("OK", () -> {})
                        .build()
                        .show();
            } finally {
                clearForm();
            }
        } else {
            AlertDialog.builder()
                    .level(AlertDialog.Level.ERROR)
                    .addOwner(TaskManagerApplication.ownerStage)
                    .title("Invalid Input")
                    .customFonts("Poppins")
                    .message("Please fill all required fields correctly!")
                    .addButton("OK", () -> {})
                    .build()
                    .show();
        }
    }

    private void clearForm(){
        taskTitleText.setText("");
        description.setText("");
        startDateText.setValue(null);
        dueDateText.setValue(null);
        priorityComboBox.setValue(null);
        progressInput.setText("");
    }

    /*Getter and Setter*/
    public boolean getIsEditing(){
        return isEditing.get();
    }
    public void setIsEditing(boolean value){
        isEditing.set(value);
    }

    public void handleCancelEditing(ActionEvent event) {
        isEditing.set(false);
        clearForm();
    }
}
