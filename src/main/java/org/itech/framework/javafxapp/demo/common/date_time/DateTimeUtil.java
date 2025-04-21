package org.itech.framework.javafxapp.demo.common.date_time;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtil {
    private static final Logger logger = LogManager.getLogger(DateTimeUtil.class);

    public static String dateToString(Date dateTime, String standardDateInputFormat) {

        if (dateTime == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(standardDateInputFormat);
        String retDate = "";
        try {
            retDate = sdf.format(dateTime);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return retDate;

    }

    public static Date stringToDate(String dateTime, String standardDateInputFormat) {

        if (standardDateInputFormat.contains("-")) {
            dateTime = dateTime.replaceAll( "/", "-");
        } else if (standardDateInputFormat.contains("/")) {
            dateTime = dateTime.replaceAll("-", "/");
        }

        SimpleDateFormat sdf = new SimpleDateFormat(standardDateInputFormat);
        try {
            return sdf.parse(dateTime);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
    public static Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    public static Date convertToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    public static LocalDate convertToLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
    public static LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static String formatUserFriendlyDueDate(Date date) {
        if (date == null) return "No due date";

        LocalDateTime dueDateTime = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        String time = dueDateTime.format(timeFormatter).toLowerCase();

        if (dueDateTime.isBefore(now)) {
            // Handle OVERDUE dates
            LocalDate dueDate = dueDateTime.toLocalDate();
            LocalDate today = now.toLocalDate();
            LocalDate yesterday = today.minusDays(1);

            if (dueDate.equals(today)) {
                return "Overdue today at " + time;
            } else if (dueDate.equals(yesterday)) {
                return "Overdue yesterday at " + time;
            } else if (dueDate.isAfter(today.minusWeeks(1))) {
                String weekday = dueDateTime.format(DateTimeFormatter.ofPattern("EEEE"));
                return "Overdue last " + weekday + " at " + time;
            } else {
                return "Overdue since " + dueDateTime.format(
                        DateTimeFormatter.ofPattern("MMM d 'at' h:mm a")
                );
            }
        } else {
            // Handle FUTURE dates
            LocalDate dueDate = dueDateTime.toLocalDate();
            LocalDate today = now.toLocalDate();
            LocalDate tomorrow = today.plusDays(1);

            if (dueDate.equals(today)) {
                return "Due today at " + time;
            } else if (dueDate.equals(tomorrow)) {
                return "Due tomorrow at " + time;
            } else if (dueDate.isBefore(today.plusWeeks(1))) {
                String weekday = dueDateTime.format(DateTimeFormatter.ofPattern("EEEE"));
                return "Due " + weekday + " at " + time;
            } else {
                return "Due " + dueDateTime.format(
                        DateTimeFormatter.ofPattern("MMM d 'at' h:mm a")
                );
            }
        }
    }
}
