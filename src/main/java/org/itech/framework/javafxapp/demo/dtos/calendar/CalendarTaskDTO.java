package org.itech.framework.javafxapp.demo.dtos.calendar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itech.framework.javafxapp.demo.dtos.TaskDTO;

import java.util.List;

@Data
@NoArgsConstructor
public class CalendarTaskDTO {
    private ObservableList<TaskDTO> tasks = FXCollections.emptyObservableList();
}
