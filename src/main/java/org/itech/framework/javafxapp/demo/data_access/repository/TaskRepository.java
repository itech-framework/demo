package org.itech.framework.javafxapp.demo.data_access.repository;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;

import io.github.itech_framework.core.utils.validator.CommonValidator;
import org.hibernate.query.Query;
import io.github.itech_framework.core.annotations.components.levels.DataAccess;
import io.github.itech_framework.jpa.repository.simple.SimpleFlexiJpaRepository;
import org.itech.framework.javafxapp.demo.common.enums.DueStatus;
import org.itech.framework.javafxapp.demo.common.enums.SortBy;
import org.itech.framework.javafxapp.demo.common.enums.TaskStatus;
import org.itech.framework.javafxapp.demo.data_access.entities.Task;
import org.itech.framework.javafxapp.demo.dtos.TaskFilterDTO;

@DataAccess
public class TaskRepository extends SimpleFlexiJpaRepository<Task, Long> {

	public List<Task> findTaskByDueStatus(DueStatus status, Integer limit) {
		return executeQuery(session -> {
			LocalDateTime now = LocalDateTime.now();
			ZoneId zone = ZoneId.systemDefault();

			QueryParameters params = new QueryParameters();
			String sql = buildSQLQueryByDueStatus(status, now, zone, params);

			Query<Task> query = session.createNativeQuery(sql, Task.class);
			params.applyTo(query);
			query.setMaxResults(limit);

			return query.getResultList();
		});
	}

	private String buildSQLQueryByDueStatus(DueStatus status, LocalDateTime referenceDateTime,
											ZoneId zone, QueryParameters params) {
		StringBuilder sb = new StringBuilder("SELECT * FROM tasks WHERE 1=1");
		sb.append(" AND status != :excludedStatus ");
		params.add("excludedStatus", TaskStatus.COMPLETE.getCode());

		switch (status) {
			case OVERDUE -> {
				sb.append("AND due_date < :now");
				params.add("now", referenceDateTime.atZone(zone).toInstant());
			}
			case DUE_TODAY -> {
				ZonedDateTime startOfDay = referenceDateTime.toLocalDate().atStartOfDay(zone);
				ZonedDateTime endOfDay = startOfDay.plusDays(1);
				sb.append("AND due_date >= :startDay AND due_date < :endDay");
				params.add("startDay", startOfDay.toInstant());
				params.add("endDay", endOfDay.toInstant());
			}
			case DUE_THIS_WEEK -> {
				ZonedDateTime startOfWeek = referenceDateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
						.toLocalDate()
						.atStartOfDay(zone);
				ZonedDateTime endOfWeek = startOfWeek.plusWeeks(1);
				sb.append("AND due_date >= :startWeek AND due_date < :endWeek");
				params.add("startWeek", startOfWeek.toInstant());
				params.add("endWeek", endOfWeek.toInstant());
			}
			case DUE_THIS_MONTH -> {
				ZonedDateTime startOfMonth = referenceDateTime.with(TemporalAdjusters.firstDayOfMonth())
						.toLocalDate()
						.atStartOfDay(zone);
				ZonedDateTime endOfMonth = startOfMonth.plusMonths(1);
				sb.append("AND due_date >= :startMonth AND due_date < :endMonth");
				params.add("startMonth", startOfMonth.toInstant());
				params.add("endMonth", endOfMonth.toInstant());
			}
			default -> throw new IllegalArgumentException("Unsupported status: " + status);
		}

		return sb.toString();
	}

	public List<Task> getTasksByFilter(TaskFilterDTO dto){
		return executeQuery(session -> {
			QueryParameters parameters = new QueryParameters();
			String sql = buildSQLQueryByFilter(dto, parameters);
			Query<Task> query = session.createNativeQuery(sql, Task.class);
			parameters.applyTo(query);

			return query.getResultList();
		});
	}

	private String buildSQLQueryByFilter(TaskFilterDTO dto, QueryParameters params){
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT * FROM tasks WHERE 1=1 ");

		if(CommonValidator.isValidObject(dto.getDate())){
			builder.append("AND DATE(due_date) = :date ");
			params.add("date", dto.getDate());
		}

		if(CommonValidator.validString(dto.getTitle())){
			builder.append("AND title LIKE :title ");
			params.add("title", "%"+dto.getTitle()+"%");
		}

		if(CommonValidator.validInteger(dto.getPriorityStatus())){
			builder.append("AND priority = :priorityStatus ");
			params.add("priorityStatus", dto.getPriorityStatus());
		}

		if(CommonValidator.validInteger(dto.getStatus())){
			builder.append("AND status = :status ");
			params.add("status", dto.getStatus());
		}

		if(CommonValidator.validInteger(dto.getSortBy())){
			SortBy sortBy = SortBy.getByCodeOrThrow(dto.getSortBy());
			switch (sortBy){
				case DUE_DATE:{
					builder.append("ORDER BY due_date DESC ");
				}break;
				case PRIORITY:{
					builder.append("ORDER BY priority DESC ");
				}break;
			}
		}

		return builder.toString();
	}

	public List<Task> findAllFutureTasks(){
		QueryParameters parameters = new QueryParameters();
		String query = "SELECT t FROM Task t WHERE t.dueDate >= :date AND t.status != :status AND t.status != :cancelStatus ORDER BY t.dueDate ASC";
		parameters.add("date", new Date());
		parameters.add("status", TaskStatus.COMPLETE.getCode());
		parameters.add("cancelStatus", TaskStatus.CANCEL.getCode());

		return findBy(query, parameters);
	}
}
