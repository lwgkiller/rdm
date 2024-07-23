/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package com.redxun.core.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 日期转化工具类
 *
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn） 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class DateUtil {

    /**
     * 应用程序的格式化符
     */
    public static final String DATE_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT_TIME = "HH:mm:ss";

    public static final String DATE_FORMAT_MON = "yyyy-MM";
    /**
     * 短日期格式
     */
    public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";

    protected static Logger logger = LogManager.getLogger(AppBeanUtil.class);

    public static Date parseDate(String dateString) {
        Date date = null;
        if (dateString.indexOf("T") == -1) {
            date = parseDate(dateString, "");
        } else {
            dateString = dateString.replace("T", " ");
            date = parseDate(dateString, DATE_FORMAT_FULL);
        }
        return date;
    }

    public static Date parseDate(String dateString, String format) {

        if (StringUtil.isEmpty(format)) {
            format = DATE_FORMAT_YMD;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date rtn = null;
        try {
            rtn = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return rtn;
    }

    /**
     * 格式化日期。
     * 
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format) {
        if (BeanUtil.isEmpty(format)) {
            format = DATE_FORMAT_FULL;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String formatDate(Date date) {
        return formatDate(date, "");
    }

    /**
     * 设置指定时间为当天的最初时间（即00时00分00秒）
     * 
     * @param date
     * @return
     */
    public static Date setStartDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * 设置指定时间为当天的结束的时间（即23时59分59秒）
     * 
     * @return
     */
    public static Date setEndDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * 获得当前日期中的月中的日号
     * 
     * @return
     */
    public static int getCurDay() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获得当前日期一年的第几周
     * 
     * @return
     */
    public static int getCurWeekOfYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获得当前日期一年的第几周
     * 
     * @param time
     *            传入的日期时间
     * @return
     */
    public static int getWeekOfYear(Date time) {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获得当前日期中的月份
     * 
     * @return
     */
    public static int getCurMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH);
    }

    /**
     * 获得当前日期中的年份
     * 
     * @return
     */
    public static int getCurYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    /**
     * 获得日期中的月中的日号
     * 
     * @param time
     *            传入的日期时间
     * @return
     */
    public static int getDay(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获得日期中的月份
     * 
     * @param time
     *            传入的日期时间
     * @return
     */
    public static int getMonth(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        return cal.get(Calendar.MONTH);
    }

    /**
     * 获得日期中的年份
     * 
     * @param time
     *            传入的日期时间
     * @return
     */
    public static int getYear(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        return cal.get(Calendar.YEAR);
    }

    /**
     * 在时间增加指定的时长。
     * 
     * @param time
     *            时间
     * @param dateUnit
     *            单位 Calendar.MINUTE
     * @param amount
     *            数量
     * @return
     */
    public static Date add(Date time, Integer dateUnit, Integer amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);

        cal.add(dateUnit, amount);
        return cal.getTime();
    }

    /**
     * 将UNIN时间戳转成
     * 
     * @param timestampString
     * @param formats
     * @return
     */
    public static String timeStamp2Date(String timestampString, String formats) {
        if (BeanUtil.isEmpty(formats))
            formats = "yyyy-MM-dd HH:mm:ss";
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
        return date;
    }

    /**
     * 将UNIX时间戳转成日期类型。 1492617620
     * 
     * @param timestampString
     * @return
     */
    public static Date timeStamp2Date(String timestampString) {
        Long timestamp = Long.parseLong(timestampString) * 1000;
        return new Date(timestamp);
    }

    public static Date addYear(Date date, int year) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.YEAR, year);
        return ca.getTime();
    }

    public static Date addMonth(Date date, int month) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.MONTH, month);
        return ca.getTime();
    }

    public static Date addDay(Date date, int day) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(5, day);
        return ca.getTime();
    }

    public static Date addOneDay(Date date) {
        return addDay(date, 1);
    }

    public static String addOneDay(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            Date dd = format.parse(date);
            calendar.setTime(dd);
            calendar.add(5, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String tmpDate = format.format(calendar.getTime());
        return tmpDate.substring(5, 7) + "/" + tmpDate.substring(8, 10) + "/" + tmpDate.substring(0, 4);
    }

    public static String addOneHour(String date) {
        String amPm = date.substring(20, 22);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();

        int hour = Integer.parseInt(date.substring(11, 13));
        try {
            if (amPm.equals("PM")) {
                hour += 12;
            }
            date = date.substring(0, 11)
                + (hour >= 10 ? Integer.valueOf(hour) : new StringBuilder().append("0").append(hour).toString())
                + date.substring(13, 19);

            Date dd = format.parse(date);
            calendar.setTime(dd);
            calendar.add(11, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String tmpDate = format.format(calendar.getTime());

        hour = Integer.parseInt(tmpDate.substring(11, 13));
        amPm = (hour >= 12) && (hour != 0) ? "PM" : "AM";
        if (amPm.equals("PM")) {
            hour -= 12;
        }
        tmpDate = tmpDate.substring(5, 7) + "/" + tmpDate.substring(8, 10) + "/" + tmpDate.substring(0, 4) + " "
            + (hour >= 10 ? Integer.valueOf(hour) : new StringBuilder().append("0").append(hour).toString())
            + tmpDate.substring(13, tmpDate.length()) + " " + amPm;

        return tmpDate;
    }

    public static boolean isBetween(Date startTime, Date endTime, Date date) {
        if ((date.after(startTime)) && (date.before(endTime))) {
            return true;
        }
        return false;
    }

    public static boolean judgeEndBigThanStart(Date startTime, Date endTime) {
        if (endTime.after(startTime)) {
            return true;
        }
        return false;
    }

    public static Date getTime(Date date, Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Calendar ca = Calendar.getInstance();
        ca.setTime(time);
        ca.set(cal.get(1), cal.get(2), cal.get(5));

        return ca.getTime();
    }

    public static String getWeekOfDate(Date date) {
        String[] weekDays = {"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(7) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    public static double betweenMinute(Date startTime, Date endTime) {
        Long seconds = Long.valueOf((endTime.getTime() - startTime.getTime()) / 1000L);
        double s = seconds.longValue() % 60.0D / 60.0D;
        DecimalFormat df = new DecimalFormat("0.00");
        double sec = Double.parseDouble(df.format(s));
        Long minute = Long.valueOf(seconds.longValue() / 60L);
        return minute.longValue() + sec;
    }

    /**
     * 获取显示时间。
     * 
     * @param minute
     * @return
     */
    public static String getDisplayTime(int minute) {
        int days = (int)(minute / (24 * 60));
        int hours = 0;
        if (days > 0) {
            minute = minute - days * (24 * 60);
            hours = (int)(minute / 60);
        }
        if (hours > 0) {
            minute = minute - hours * 60;
        }
        if (days > 0) {
            return days + "天" + hours + "小时" + minute + "分钟";
        } else if (hours > 0) {
            return hours + "小时" + minute + "分钟";
        }
        return minute + "分钟";
    }

    /**
     * 计算时间。
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    public static String getDisplayTime(Date startTime, Date endTime) {
        double minute = betweenMinute(startTime, endTime);
        int days = (int)(minute / (24 * 60));
        int hours = 0;
        if (days > 0) {
            minute = minute - days * (24 * 60);
            hours = (int)(minute / 60);
        }
        if (hours > 0) {
            minute = minute - hours * 60;
        }
        if (days > 0) {
            return days + "天" + hours + "小时" + minute + "分钟";
        } else if (hours > 0) {
            return hours + "小时" + minute + "分钟";
        }
        return minute + "分钟";
    }

    public static double betweenHour(Date startTime, Date endTime) {
        Long minutes = Long.valueOf((endTime.getTime() - startTime.getTime()) / 1000L / 60L);
        double m = minutes.longValue() % 60.0D / 60.0D;
        DecimalFormat df = new DecimalFormat("0.00");
        double min = Double.parseDouble(df.format(m));

        Long hour = Long.valueOf(minutes.longValue() / 60L);
        return hour.longValue() + min;
    }

    public static double betweenHour(Date startTime, Date endTime, Double restMinutes) {
        Long minutes = Long.valueOf((endTime.getTime() - startTime.getTime()) / 1000L / 60L);
        Long ms = Long.valueOf((long)(minutes.longValue() - restMinutes.doubleValue()));

        double m = ms.longValue() % 60.0D / 60.0D;
        DecimalFormat df = new DecimalFormat("0.00");
        double min = Double.parseDouble(df.format(m));

        Long hour = Long.valueOf(ms.longValue() / 60L);
        return hour.longValue() + min;
    }

    public static String[] getDaysBetweenDate(Date startTime, Date endTime) {
        String[] dateArr = null;
        try {
            Integer day = Integer.valueOf(daysBetween(startTime, endTime));

            dateArr = new String[day.intValue() + 1];
            for (int i = 0; i < dateArr.length; i++) {
                if (i == 0) {
                    dateArr[i] = formatDate(startTime);
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startTime);
                    calendar.add(5, i);
                    dateArr[i] = formatDate(calendar.getTime());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateArr;
    }

    public static Date getNextTime(Date date, int field, int amount) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(field, amount);
        return ca.getTime();
    }

    public static Date addHour(Date date, int hour) {
        return getNextTime(date, 11, hour);
    }

    public static Date addHour(Date date, double time) {
        int hour = (int)time;
        double minute1 = (time - hour) * 60.0D;
        int minute = (int)minute1;
        int second = (int)((minute1 - minute) * 60.0D);
        Date dateHour = getNextTime(date, 11, hour);
        Date dateMinute = getNextTime(dateHour, 12, minute);
        return getNextTime(dateMinute, 13, second);
    }

    public static Date addMinute(Date date, int minute) {
        return getNextTime(date, 12, minute);
    }

    /**
     * 计算两个日期之间相差的天数
     * 
     * @param smdate
     *            较小的时间
     * @param bdate
     *            较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        long days = (bdate.getTime() - smdate.getTime()) / (1000 * 3600 * 24);
        return (int)days;
    }

    /**
     * 判断是不是某个月的最后一天
     * 
     * @return
     */
    public static boolean judgeIsLastDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return dayOfMonth == lastDayOfMonth;
    }
}
