package org.itech.framework.javafxapp.demo.utils.components;

import io.github.itech_framework.core.annotations.constructor.DefaultConstructor;
import io.github.itech_framework.java_fx.annotations.components.FxComponent;
import io.github.itech_framework.java_fx.annotations.components.Root;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import org.itech.framework.javafxapp.demo.common.enums.DueStatus;
import org.itech.framework.javafxapp.demo.common.enums.PriorityStatus;
import org.itech.framework.javafxapp.demo.dtos.TaskDTO;

@FxComponent("/views/components/task-card.fxml")
public class TaskCardList extends ListCell<TaskDTO> {
    @FXML @Root
    private VBox root;

    @FXML private SvgIcon priorityIcon;
    @FXML private Label titleLabel;
    @FXML private Label dueDateLabel;
    @FXML private Label descriptionLabel;

    private DueStatus dueStatus;

    @DefaultConstructor
    public TaskCardList(){}

    public TaskCardList(DueStatus status) {
        this.dueStatus = status;
        configureCellStyle();
    }

    private void configureCellStyle() {
        setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        setBackground(Background.EMPTY);

        getStyleClass().add("task-list-cell");
    }

    private String getStyleClassByDue() {
        return switch (dueStatus) {
            case OVERDUE -> "overdue";
            case DUE_THIS_WEEK -> "due-this-week";
            case DUE_THIS_MONTH -> "due-this-month";
            default -> "due-today";
        };
    }

    @Override
    protected void updateItem(TaskDTO item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            clearBindings();
        } else {
            bindData(item);
            setGraphic(root);
        }
        setText(null);
    }

    private void bindData(TaskDTO task) {
        titleLabel.textProperty().bind(task.titleProperty());
        dueDateLabel.textProperty().bind(task.dueDateDetailDescProperty());
        descriptionLabel.textProperty().bind(task.descriptionProperty());
        updatePriorityIcon(task.getPriority());
    }

    private void clearBindings() {
        titleLabel.textProperty().unbind();
        dueDateLabel.textProperty().unbind();
        descriptionLabel.textProperty().unbind();
    }

    private void updatePriorityIcon(int priority) {
        priorityIcon.getStyleClass().setAll("priority-icon"); // Reset styles
        priorityIcon.setIconSize(25);

        PriorityStatus status = PriorityStatus.getByCodeOrThrow(priority);
        String svgFile = switch (status) {
            case HIGH -> "/static/svgs/angle-double-up.svg";
            case CRITICAL -> "/static/svgs/priority-critical-icon.svg";
            case LOW -> "/static/svgs/angle-double-down.svg";
            default -> "/static/svgs/equals.svg";
        };

        priorityIcon.setSvgFile(svgFile);
        priorityIcon.getStyleClass().add(status.name().toLowerCase());
    }
}
