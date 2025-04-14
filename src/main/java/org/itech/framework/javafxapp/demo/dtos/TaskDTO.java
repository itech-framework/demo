package org.itech.framework.javafxapp.demo.dtos;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.itech.framework.javafxapp.demo.common.date_time.DateTimeConstant;
import org.itech.framework.javafxapp.demo.common.date_time.DateTimeUtil;
import org.itech.framework.javafxapp.demo.common.enums.PriorityStatus;
import org.itech.framework.javafxapp.demo.common.enums.TaskStatus;
import org.itech.framework.javafxapp.demo.data_access.entities.Task;

import java.util.Date;

public class TaskDTO extends CommonDTO {
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<Date> startDate = new SimpleObjectProperty<>();
    private final StringProperty startDateDesc = new SimpleStringProperty();
    private final ObjectProperty<Date> dueDate = new SimpleObjectProperty<>();
    private final StringProperty dueDateDesc = new SimpleStringProperty();
    private final IntegerProperty progress = new SimpleIntegerProperty();
    private final IntegerProperty status = new SimpleIntegerProperty();
    private final StringProperty statusDesc = new SimpleStringProperty();
    private final IntegerProperty priority = new SimpleIntegerProperty();
    private final StringProperty priorityDesc = new SimpleStringProperty();

    public TaskDTO() {}

    public TaskDTO(Task t) {
        if (t == null) return;
        setTitle(t.getTitle());
        setDescription(t.getDescription());
        setDueDate(t.getDueDate());
        setStartDate(t.getStartDate());
        setPriority(t.getPriority());
        setStatus(t.getStatus());
        setProgress(t.getProgress());
        /*setStatusDesc(TaskStatus.getDescByCode(getStatus()));
        setPriorityDesc(PriorityStatus.getDescByCode(getPriority()));*/
        statusDesc.bind(Bindings.createStringBinding(() ->
                        TaskStatus.getDescByCode(status.get()),
                status
        ));
        priorityDesc.bind(Bindings.createStringBinding(() ->
                        PriorityStatus.getDescByCode(priority.get()),
                priority
        ));

        if (getDueDate() != null) {
            setDueDateDesc(DateTimeUtil.dateToString(
                    getDueDate(), DateTimeConstant.STANDARD_12_HOUR_DATE_MINUTE_FORMAT
            ));
        }

        if (getStartDate() != null) {
            setStartDateDesc(DateTimeUtil.dateToString(
                    getStartDate(), DateTimeConstant.STANDARD_12_HOUR_DATE_MINUTE_FORMAT
            ));
        }

        setFields(t);
    }

    // Title
    public String getTitle() { return title.get(); }
    public StringProperty titleProperty() { return title; }
    public String getDescription(){return description.get();}
    public StringProperty descriptionProperty(){return description;}
    public void setDescription(String description){this.description.set(description);}
    public void setTitle(String title) { this.title.set(title); }

    // Start Date
    public Date getStartDate() { return startDate.get(); }
    public ObjectProperty<Date> startDateProperty() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate.set(startDate); }

    // Start Date Desc
    public String getStartDateDesc() { return startDateDesc.get(); }
    public StringProperty startDateDescProperty() { return startDateDesc; }
    public void setStartDateDesc(String startDateDesc) { this.startDateDesc.set(startDateDesc); }

    // Due Date
    public Date getDueDate() { return dueDate.get(); }
    public ObjectProperty<Date> dueDateProperty() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate.set(dueDate); }

    // Due Date Desc
    public String getDueDateDesc() { return dueDateDesc.get(); }
    public StringProperty dueDateDescProperty() { return dueDateDesc; }
    public void setDueDateDesc(String dueDateDesc) { this.dueDateDesc.set(dueDateDesc); }

    // Progress
    public int getProgress() { return progress.get(); }
    public IntegerProperty progressProperty() { return progress; }
    public void setProgress(int progress) { this.progress.set(progress); }

    // Status
    public int getStatus() { return status.get(); }
    public IntegerProperty statusProperty() { return status; }
    public void setStatus(int status) { this.status.set(status); }

    // Status Desc
    public String getStatusDesc() { return statusDesc.get(); }
    public StringProperty statusDescProperty() { return statusDesc; }
    public void setStatusDesc(String statusDesc) { this.statusDesc.set(statusDesc); }

    // Priority
    public int getPriority() { return priority.get(); }
    public IntegerProperty priorityProperty() { return priority; }
    public void setPriority(int priority) { this.priority.set(priority); }

    // Priority Desc
    public String getPriorityDesc() { return priorityDesc.get(); }
    public StringProperty priorityDescProperty() { return priorityDesc; }
    public void setPriorityDesc(String priorityDesc) { this.priorityDesc.set(priorityDesc); }
}