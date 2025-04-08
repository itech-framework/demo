package org.itech.framework.javafxapp.demo.data_access.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.itech.framework.javafxapp.demo.common.TableNames;
import org.itech.framework.javafxapp.demo.data_access.BasedEntity;

import java.util.Date;

@Entity
@Table(name = TableNames.TASK_TABLE)
@Data
@EqualsAndHashCode(callSuper = true)
public class Task extends BasedEntity {
    private String title;
    private String description;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "due_date")
    private Date dueDate;
    private Integer progress;
    private Integer status;
    private Integer priority;
}
