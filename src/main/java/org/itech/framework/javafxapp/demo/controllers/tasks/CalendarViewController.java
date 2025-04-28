package org.itech.framework.javafxapp.demo.controllers.tasks;

import io.github.itech_framework.core.annotations.methods.OnInit;
import io.github.itech_framework.java_fx.annotations.FxController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.itech.framework.javafxapp.demo.dtos.TaskDTO;
import org.itech.framework.javafxapp.demo.dtos.calendar.CalendarTaskDTO;

import java.time.LocalDateTime;

@FxController
public class CalendarViewController {

    @FXML
    private TableView<CalendarTaskDTO> tableView;

    @OnInit
    private void onInit(){
        Platform.runLater(()->{

        });
    }
}
