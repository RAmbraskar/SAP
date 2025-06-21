package com.ea.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private DateUtil() {
    }

    public static String getDateMinusDays(int numberOfDaysFromCurrentDate, String dateFormat) {
        return LocalDateTime.now().minusDays(numberOfDaysFromCurrentDate).format(DateTimeFormatter.ofPattern(dateFormat));
    }
    public static String getCurrentDate(String dateFormat) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(dateFormat));
    }

    public static String getDateMinusMonth(int numberOfMonthsFromCurrentDate, String dateFormat) {
        return LocalDateTime.now().minusMonths(numberOfMonthsFromCurrentDate).format(DateTimeFormatter.ofPattern(dateFormat));
    }

    public static String getLastDayOfPreviousMonth(String dateFormat) {
        return LocalDate.now().withDayOfMonth(1).minusDays(1).atStartOfDay().format(DateTimeFormatter.ofPattern(dateFormat));
    }

    public static String getFirstDayOfMonth(long monthsToSubtract, String dateFormat) {
        return LocalDate.now().minusMonths(monthsToSubtract).withDayOfMonth(1).atStartOfDay().format(DateTimeFormatter.ofPattern(dateFormat));
    }

    public static String getLastDayOfMonth(long monthsToSubtract, String dateFormat) {
        return LocalDate.now().minusMonths(monthsToSubtract).withDayOfMonth(1).minusDays(1).atStartOfDay().format(DateTimeFormatter.ofPattern(dateFormat));
    }

    public static String changeFormat(String srcDate, String srcDateFormat, String newDateFormat) {
        return LocalDate.parse(srcDate, DateTimeFormatter.ofPattern(srcDateFormat)).format(DateTimeFormatter.ofPattern(newDateFormat));
    }
    public static long getCurrentEpochSecond() {
        return Instant.now().getEpochSecond();
    }
}
