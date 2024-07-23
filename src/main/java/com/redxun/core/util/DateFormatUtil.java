package com.redxun.core.util;

import org.apache.commons.lang.StringUtils;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateFormatUtil {
    public static java.util.Date parse(String dateString) throws ParseException {
        if ((dateString.trim().indexOf(" ") > 0) && (dateString.trim().indexOf(".") > 0)) {
            return new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(dateString).getTime());
        }
        if (dateString.trim().indexOf(" ") > 0) {
            if (dateString.trim().indexOf(":") != dateString.trim().lastIndexOf(":")) {
                return new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString).getTime());
            }
            return new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString).getTime());
        }
        if (dateString.indexOf(":") > 0) {
            if (dateString.trim().indexOf(":") != dateString.trim().lastIndexOf(":")) {
                return new Time(new SimpleDateFormat("HH:mm:ss").parse(dateString).getTime());
            }
            return new Time(new SimpleDateFormat("HH:mm").parse(dateString).getTime());
        }
        return new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(dateString).getTime());
    }

    public static java.util.Date parseTime(String dateString) throws ParseException {
        if (dateString.trim().indexOf(" ") > 0) {
            String[] d = dateString.trim().split(" ");
            if (dateString.trim().indexOf(":") != dateString.trim().lastIndexOf(":")) {
                return new Timestamp(new SimpleDateFormat("HH:mm:ss").parse(d[1]).getTime());
            }
            return new Timestamp(new SimpleDateFormat("HH:mm").parse(d[1]).getTime());
        }
        if (dateString.indexOf(":") > 0) {
            if (dateString.trim().indexOf(":") != dateString.trim().lastIndexOf(":")) {
                return new Time(new SimpleDateFormat("HH:mm:ss").parse(dateString).getTime());
            }
            return new Time(new SimpleDateFormat("HH:mm").parse(dateString).getTime());
        }
        return new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString).getTime());
    }

    public static String format(java.util.Date date) {
        if (date == null) {
            return "";
        }
        if ((date instanceof Timestamp)) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date);
        }
        if ((date instanceof Time)) {
            return new SimpleDateFormat("HH:mm:ss").format(date);
        }
        if ((date instanceof java.sql.Date)) {
            return new SimpleDateFormat("yyyy-MM-dd").format(date);
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static java.util.Date parse(String dateString, String style) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(style);
        return dateFormat.parse(dateString);
    }

    public static String format(java.util.Date date, String style) {
        if (date == null) {
            return "";
        }
        DateFormat dateFormat = new SimpleDateFormat(style);
        return dateFormat.format(date);
    }

    public static java.util.Date parseDate(String dateString) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
    }

    public static String formatDate(java.util.Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static java.util.Date parseDateTime(String dateString) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
    }

    public static String formatDateTime(java.util.Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String formatTimeNoSecond(java.util.Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

    public static java.util.Date parseTimeNoSecond(String dateString) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString);
    }

    public static String getNowByString(String style) {
        if (StringUtils.isBlank(style)) {
            style = "yyyy-MM-dd HH:mm:ss";
        }
        return format(new java.util.Date(), style);
    }

    public static String getNowUTCDateStr(String formatStr) {
        String todayStart = DateFormatUtil.getNowByString(formatStr);
        String result = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(todayStart, formatStr), -8), formatStr);
        return result;
    }
}
