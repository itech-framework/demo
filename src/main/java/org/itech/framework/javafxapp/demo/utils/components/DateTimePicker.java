package org.itech.framework.javafxapp.demo.utils.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// Custom DateTimePicker class
public class DateTimePicker extends HBox {
    private final DatePicker datePicker = new DatePicker();
    private final ComboBox<String> timeCombo = new ComboBox<>();

    public DateTimePicker() {
        super(5);
        initialize();
    }

    private void initialize() {
        // Populate time options (every 15 minutes)
        ObservableList<String> times = FXCollections.observableArrayList();
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 60; m += 15) {
                times.add(String.format("%02d:%02d", h, m));
            }
        }
        timeCombo.setItems(times);

        // Style components
        datePicker.getStyleClass().add("modern-datepicker");
        timeCombo.getStyleClass().add("modern-combo");
        timeCombo.setPromptText("Select time");

        getChildren().addAll(datePicker, timeCombo);
    }

    public LocalDateTime getDateTime() {
        if (datePicker.getValue() == null || timeCombo.getValue() == null) {
            return null;
        }

        LocalDate date = datePicker.getValue();
        String[] timeParts = timeCombo.getValue().split(":");
        return LocalDateTime.of(date,
                LocalTime.of(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1])));
    }

    public void setDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            datePicker.setValue(dateTime.toLocalDate());
            timeCombo.setValue(String.format("%02d:%02d",
                    dateTime.getHour(), dateTime.getMinute()));
        } else {
            datePicker.setValue(null);
            timeCombo.setValue(null);
        }
    }
}
