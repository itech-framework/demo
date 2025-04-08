package org.itech.framework.javafxapp.demo.services;

import org.itech.framework.javafxapp.demo.dtos.TaskDTO;

import java.util.List;

public interface TaskService {
    TaskDTO manageTask(TaskDTO taskDTO) throws Exception;
    List<TaskDTO> getAllTask();
    boolean deleteTask(Long taskId) throws Exception;
}
