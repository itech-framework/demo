package org.itech.framework.javafxapp.demo.dtos;

import javafx.beans.property.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.itech.framework.javafxapp.demo.common.enums.PriorityStatus;
import org.itech.framework.javafxapp.demo.common.enums.SortBy;
import org.itech.framework.javafxapp.demo.common.enums.TaskStatus;

@Getter
@Setter
@NoArgsConstructor
public class TaskFilterDTO {
    private String title;
    private Integer sortBy;
    private Integer priorityStatus;
    private Integer status;
}