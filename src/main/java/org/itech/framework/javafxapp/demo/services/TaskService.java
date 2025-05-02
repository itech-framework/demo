package org.itech.framework.javafxapp.demo.services;

import org.itech.framework.javafxapp.demo.common.enums.DueStatus;
import org.itech.framework.javafxapp.demo.dtos.TaskDTO;
import org.itech.framework.javafxapp.demo.dtos.TaskFilterDTO;

import java.util.List;

public interface TaskService {
    TaskDTO manageTask(TaskDTO taskDTO) throws Exception;
    List<TaskDTO> getAllTask();
    boolean deleteTask(Long taskId) throws Exception;
    
    List<TaskDTO> getTasksByDueStatus(DueStatus status, Integer limit);

    List<TaskDTO> getTasksByFilter(TaskFilterDTO dto, boolean includeOverdue);

    List<TaskDTO> getAllFutureSchedule();
}
