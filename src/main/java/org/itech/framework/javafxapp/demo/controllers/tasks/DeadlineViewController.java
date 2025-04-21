package org.itech.framework.javafxapp.demo.controllers.tasks;

import io.github.itech_framework.java_fx.loader.FxComponentLoader;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.itech_framework.core.annotations.methods.OnInit;
import io.github.itech_framework.core.annotations.reactives.Rx;
import io.github.itech_framework.java_fx.annotations.FxController;
import io.github.itech_framework.java_fx.ui.dialog.AlertDialog;
import io.github.itech_framework.java_fx.ui.dialog.AlertDialog.Level;
import io.github.itech_framework.java_fx.utils.concurrent.BackgroundTaskService;
import org.itech.framework.javafxapp.demo.TaskManagerApplication;
import org.itech.framework.javafxapp.demo.common.enums.DueStatus;
import org.itech.framework.javafxapp.demo.common.enums.PriorityStatus;
import org.itech.framework.javafxapp.demo.dtos.TaskDTO;
import org.itech.framework.javafxapp.demo.services.TaskService;
import org.itech.framework.javafxapp.demo.utils.components.SvgIcon;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.itech.framework.javafxapp.demo.utils.components.TaskCardList;

import java.util.List;

@FxController
public class DeadlineViewController {
	private final Logger logger = LogManager.getLogger(getClass());

	// Overdue Section
	@FXML private ListView<TaskDTO> overdueTaskListView;
	@FXML private StackPane overdueLoadingOverlay;
	@FXML private ProgressIndicator overdueLoadingSpinner;
	private final ObservableList<TaskDTO> overdueTasks = FXCollections.observableArrayList();

	// Due Today Section
	@FXML private ListView<TaskDTO> dueTodayTaskListView;
	@FXML private StackPane todayLoadingOverlay;
	@FXML private ProgressIndicator todayLoadingSpinner;
	private final ObservableList<TaskDTO> dueTodayTasks = FXCollections.observableArrayList();

	// Week Section
	@FXML private ListView<TaskDTO> weekTaskListView;
	@FXML private StackPane weekLoadingOverlay;
	@FXML private ProgressIndicator weekLoadingSpinner;
	private final ObservableList<TaskDTO> weekTasks = FXCollections.observableArrayList();

	// Month Section
	@FXML private ListView<TaskDTO> monthTaskListView;
	@FXML private StackPane monthLoadingOverlay;
	@FXML private ProgressIndicator monthLoadingSpinner;
	private final ObservableList<TaskDTO> monthTasks = FXCollections.observableArrayList();

	@Rx private TaskService taskService;
	private final int limitTasks = 4;

	@OnInit
	private void onInit() {
		configureListViews();
		fetchAllTasks();
	}

	private void configureListViews() {
		configureListView(overdueTaskListView, DueStatus.OVERDUE);
		configureListView(dueTodayTaskListView, DueStatus.DUE_TODAY);
		configureListView(weekTaskListView, DueStatus.DUE_THIS_WEEK);
		configureListView(monthTaskListView, DueStatus.DUE_THIS_MONTH);
	}

	private void configureListView(ListView<TaskDTO> listView, DueStatus status) {
		listView.setCellFactory(lv -> FxComponentLoader.load(TaskCardList.class, status));
		listView.setFocusTraversable(false);

		switch(status) {
			case OVERDUE -> listView.setItems(overdueTasks);
			case DUE_TODAY -> listView.setItems(dueTodayTasks);
			case DUE_THIS_WEEK -> listView.setItems(weekTasks);
			case DUE_THIS_MONTH -> listView.setItems(monthTasks);
		}
	}

	private void fetchAllTasks() {
		fetchTasks(DueStatus.OVERDUE, overdueTasks, overdueLoadingOverlay, overdueLoadingSpinner);
		fetchTasks(DueStatus.DUE_TODAY, dueTodayTasks, todayLoadingOverlay, todayLoadingSpinner);
		fetchTasks(DueStatus.DUE_THIS_WEEK, weekTasks, weekLoadingOverlay, weekLoadingSpinner);
		fetchTasks(DueStatus.DUE_THIS_MONTH, monthTasks, monthLoadingOverlay, monthLoadingSpinner);
	}

	private void fetchTasks(DueStatus status, ObservableList<TaskDTO> targetList,
							StackPane loadingOverlay, ProgressIndicator spinner) {
		showLoadingIndicator(loadingOverlay, spinner, true);

		BackgroundTaskService.getInstance().executeTask(
				() -> taskService.getTasksByDueStatus(status, limitTasks),
				data -> handleDataLoad(data, targetList, loadingOverlay, spinner, status),
				ex -> handleError(ex, status),
				progress -> updateProgress(spinner, progress)
		);
	}

	private void handleDataLoad(List<TaskDTO> data, ObservableList<TaskDTO> targetList,
								StackPane loadingOverlay, ProgressIndicator spinner,
								DueStatus status) {
		Platform.runLater(() -> {
			targetList.setAll(data);
			showLoadingIndicator(loadingOverlay, spinner, false);
			playFadeAnimation(getListViewByStatus(status));
		});
	}

	private void handleError(Throwable ex, DueStatus status) {
		Platform.runLater(() -> {
			AlertDialog.builder()
					.level(Level.ERROR)
					.addOwner(TaskManagerApplication.ownerStage)
					.title("Loading Error - " + status)
					.message(ex.getMessage())
					.customFonts("Poppins")
					.build()
					.show();
		});
		logger.error("Error loading {} tasks: {}", status, ex.getMessage());
	}

	private ListView<TaskDTO> getListViewByStatus(DueStatus status) {
		return switch(status) {
			case OVERDUE -> overdueTaskListView;
			case DUE_TODAY -> dueTodayTaskListView;
			case DUE_THIS_WEEK -> weekTaskListView;
			case DUE_THIS_MONTH -> monthTaskListView;
		};
	}

	private void showLoadingIndicator(StackPane overlay, ProgressIndicator spinner, boolean show) {
		Platform.runLater(() -> {
			if(overlay == null || spinner == null) return;
			overlay.setVisible(show);
			overlay.setManaged(show);
			spinner.setVisible(show);
		});
	}

	private void updateProgress(ProgressIndicator spinner, double progress) {
		Platform.runLater(() -> spinner.setProgress(progress));
	}

	private void playFadeAnimation(ListView<TaskDTO> listView) {
		FadeTransition fadeIn = new FadeTransition(Duration.millis(300), listView);
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);
		fadeIn.play();
	}
}
