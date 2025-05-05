package org.itech.framework.javafxapp.demo.services;

import java.util.*;

import io.github.itech_framework.core.annotations.components.levels.BusinessLogic;
import io.github.itech_framework.core.annotations.reactives.Rx;
import io.github.itech_framework.core.utils.validator.CommonValidator;
import org.itech.framework.javafxapp.demo.common.enums.DueStatus;
import org.itech.framework.javafxapp.demo.common.enums.TaskStatus;
import org.itech.framework.javafxapp.demo.data_access.entities.Task;
import org.itech.framework.javafxapp.demo.data_access.repository.TaskRepository;
import org.itech.framework.javafxapp.demo.dtos.TaskDTO;
import org.itech.framework.javafxapp.demo.dtos.TaskFilterDTO;

@BusinessLogic
public class TaskServiceImpl implements TaskService{

    @Rx
    TaskRepository taskRepository;

    @Override
    public TaskDTO manageTask(TaskDTO taskDTO) throws Exception {
        if(!CommonValidator.isValidObject(taskDTO)) return null;
        Task task = null;
        if(CommonValidator.validLong(taskDTO.getId())){
            Optional<Task> taskOpt = this.taskRepository.findById(taskDTO.getId());
            if(taskOpt.isPresent()){
                task = taskOpt.get();
                task.setUpdatedDateTime(new Date());
            }else{
                throw new Exception("No reference object found");
            }
        }else{
            task = new Task();
            task.setCreatedDateTime(new Date());
        }

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setStartDate(taskDTO.getStartDate());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        task.setProgress(taskDTO.getProgress());

        Task updated = this.taskRepository.save(task);

        return new TaskDTO(updated);
    }

    @Override
    public List<TaskDTO> getAllTask() {
        List<Task> tasks = this.taskRepository.findAll();
        if(CommonValidator.validList(tasks)){
            analyzeOverDueTasks(tasks);
            return tasks.stream().map(TaskDTO::new).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public boolean deleteTask(Long taskId) throws Exception {
        Optional<Task> taskOpt = this.taskRepository.findById(taskId);
        if(taskOpt.isPresent()){
            this.taskRepository.deleteById(taskId);
            return true;
        }else{
            throw new Exception("No reference found!");
        }
    }

	@Override
	public List<TaskDTO> getTasksByDueStatus(DueStatus status, Integer limit) {
		List<Task> tasks = this.taskRepository.findTaskByDueStatus(status, limit);
		if(CommonValidator.validList(tasks)) {
            analyzeOverDueTasks(tasks);
			return tasks.stream().map(TaskDTO::new).toList();
		}
		return Collections.emptyList();
	}

    @Override
    public List<TaskDTO> getTasksByFilter(TaskFilterDTO dto, boolean includeOverdue) {
        List<Task> tasks = this.taskRepository.getTasksByFilter(dto);
        List<Task> overdueTasks = new ArrayList<>();
        if(includeOverdue){
            overdueTasks = this.taskRepository.findTaskByDueStatus(DueStatus.OVERDUE,-1);
        }
        List<Task> allTasks = new ArrayList<>(overdueTasks);
        allTasks.addAll(tasks);
        if(CommonValidator.validList(allTasks)){
            return allTasks.stream().map(TaskDTO::new).toList();
        }
        return List.of();
    }

    @Override
    public List<TaskDTO> getAllFutureSchedule() {
        List<Task> tasks = this.taskRepository.findAllFutureTasks();
        if(CommonValidator.validList(tasks)){
            return tasks.stream().map(TaskDTO::new).toList();
        }
        return List.of();
    }

    private void analyzeOverDueTasks(List<Task> tasks){
        for(Task task: tasks){
            if(!TaskStatus.COMPLETE.getCode().equals(task.getStatus())
                && !TaskStatus.CANCEL.getCode().equals(task.getStatus())){
                Date current = new Date();
                Date dueDate = task.getDueDate();
                if(dueDate != null && dueDate.before(current)){
                    task.setStatus(TaskStatus.OVER_DUE.getCode());
                    this.taskRepository.save(task);
                }
            }
        }
    }
}
