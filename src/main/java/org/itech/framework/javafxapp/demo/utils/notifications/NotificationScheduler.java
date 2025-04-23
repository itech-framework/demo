package org.itech.framework.javafxapp.demo.utils.notifications;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.*;

import io.github.itech_framework.core.annotations.components.Component;
import io.github.itech_framework.core.annotations.constructor.DefaultConstructor;
import io.github.itech_framework.core.annotations.methods.PreDestroy;
import io.github.itech_framework.java_fx.ui.dialog.AlertDialog;
import javafx.application.Platform;
import org.controlsfx.control.Notifications;
import org.itech.framework.javafxapp.demo.TaskManagerApplication;
import org.itech.framework.javafxapp.demo.common.date_time.DateTimeUtil;
import org.itech.framework.javafxapp.demo.dtos.TaskDTO;

import javax.swing.*;

@Component
public class NotificationScheduler {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Map<TaskDTO, List<ScheduledFuture<?>>> taskSchedules = new HashMap<>();

    @DefaultConstructor
    public NotificationScheduler() {}

    public void scheduleNotifications(TaskDTO task) {
        LocalDateTime dueDate = DateTimeUtil.convertToLocalDateTime(task.getDueDate());
        if (dueDate == null) return;

        // Cancel existing notifications for this task first
        cancelNotifications(task);

        List<ScheduledFuture<?>> futures = new ArrayList<>();
        futures.add(scheduleNotification(dueDate, 30, task.getTitle(), "30 minutes remaining!"));
        futures.add(scheduleNotification(dueDate, 15, task.getTitle(), "15 minutes remaining!"));
        futures.add(scheduleNotification(dueDate, 5, task.getTitle(), "5 minutes remaining!"));

        // Remove null futures (for notifications that are in the past)
        futures.removeIf(Objects::isNull);
        taskSchedules.put(task, futures);
    }

    private ScheduledFuture<?> scheduleNotification(LocalDateTime dueDate,
                                                    int minutesBefore,
                                                    String title,
                                                    String message) {
        LocalDateTime notifyTime = dueDate.minusMinutes(minutesBefore);
        Duration delay = Duration.between(LocalDateTime.now(), notifyTime);

        if (delay.isNegative()) return null;

        Runnable notificationTask = () -> Platform.runLater(() -> showAlert(title, message));
        return executor.schedule(
                notificationTask,
                delay.toMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    public void cancelNotifications(TaskDTO task) {
        List<ScheduledFuture<?>> futures = taskSchedules.get(task);
        if (futures != null) {
            futures.forEach(future -> {
                if (future != null && !future.isDone()) {
                    future.cancel(false);
                }
            });
            taskSchedules.remove(task);
        }
    }

    private void showAlert(String title, String message) {
        if (SystemTray.isSupported()) {
            Platform.runLater(()->{
                AlertDialog.builder()
                        .addOwner(TaskManagerApplication.ownerStage)
                        .level(AlertDialog.Level.INFO)
                        .title(title)
                        .message(message)
                        .build()
                        .show();
            });

            // Use SwingUtilities to ensure AWT runs on the correct thread
            SwingUtilities.invokeLater(() -> {
                try {
                    SystemTray tray = SystemTray.getSystemTray();

                    // Load an icon for the notification (replace "icon.png" with your image)
                    Image icon = Toolkit.getDefaultToolkit().createImage(
                            getClass().getResource("/static/images/task-icon.png")
                    );

                    // Create a tray icon
                    TrayIcon trayIcon = new TrayIcon(icon, "Task Manager");
                    trayIcon.setImageAutoSize(true);

                    // Add the tray icon temporarily
                    tray.add(trayIcon);

                    // Show the notification
                    trayIcon.displayMessage(
                            title,
                            message,
                            TrayIcon.MessageType.INFO
                    );

                    // Remove the tray icon after 5 seconds
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            tray.remove(trayIcon);
                        }
                    }, 5000);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            });
        } else{
            Platform.runLater(()->{

                AlertDialog.builder()
                        .addOwner(TaskManagerApplication.ownerStage)
                        .level(AlertDialog.Level.INFO)
                        .title(title)
                        .message(message)
                        .build()
                        .show();
            });
        }

    }

    @PreDestroy
    public void shutdown() {
        taskSchedules.values().forEach(futures ->
                futures.forEach(future -> future.cancel(false))
        );
        taskSchedules.clear();
        executor.shutdown();
    }
}