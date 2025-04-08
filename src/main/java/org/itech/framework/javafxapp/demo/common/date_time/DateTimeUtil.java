package org.itech.framework.javafxapp.demo.common.date_time;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
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
}
