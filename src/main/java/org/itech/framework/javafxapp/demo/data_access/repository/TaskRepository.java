package org.itech.framework.javafxapp.demo.data_access.repository;

import org.itech.framework.fx.core.annotations.components.levels.DataAccess;
import org.itech.framework.fx.jpa.repository.simple.SimpleFlexiJpaRepository;
import org.itech.framework.javafxapp.demo.data_access.entities.Task;

import java.util.List;

@DataAccess
public class TaskRepository extends SimpleFlexiJpaRepository<Task, Long> {
}
