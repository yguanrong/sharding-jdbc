package com.yuzi.utils;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.logging.log4j.util.Strings;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author tianpengfei
 * @date 2022年06月06日 10:47
 */
@Slf4j
public class DateUtil {
    public static final String DTF_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String DTFYMDHMS = "yyyyMMddHHmmss";
    public static final String DTF_YMD = "yyyy-MM-dd";
    public static final String DTF_YMD_1 = "yyyy/MM/dd";
    public static final String DTFYMD = "yyyyMMdd";
    public static final String DTFYM = "yyyyMM";
    public static final String DAY = "day";
    public static final String HOURS = "hour";
    public static final String MINUTE = "minute";
    public static final String DTF_YM = "yyyy-MM";
    public static final String DTF_YMD_HMSS = "yyyy-MM-dd HH:mm:ss.S";
    public static final String DTF_YMD_HMSS_2 = "yyyy/M/d H:m:s";
    public static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static DateTimeFormatter DTF_YMD_HMS_FORMAT = DateTimeFormatter.ofPattern(DTF_YMD_HMS);

    public static DateTimeFormatter DTF_YMD_FORMAT = DateTimeFormatter.ofPattern(DTF_YMD);

    public static DateTimeFormatter DTFYMD_FORMAT = DateTimeFormatter.ofPattern(DateUtil.DTFYMD);

    public static DateTimeFormatter DTFYM_FORMAT = DateTimeFormatter.ofPattern(DateUtil.DTFYM);



    /**
     * 日期枚举
     *
     * @author pujiang
     * @date 2019年6月18日 上午10:46:06
     */
    public enum DateType {
        /**
         * 年
         */
        YEAR,
        /**
         * 月
         */
        MONTH,
        /**
         * 周
         */
        WEEK,
        /**
         * 天
         */
        DAY,
        /**
         * 时
         */
        HH,
        /**
         * 分
         */
        MI,
        /**
         * 秒
         */
        SS
    }


    public static String getYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date d = cal.getTime();

        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
        return sp.format(d);
    }

    public static Date getYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static String getYesterdayTime() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date d = cal.getTime();

        SimpleDateFormat sp = new SimpleDateFormat(DTF_YMD_HMS);
        return sp.format(d);
    }

    /**
     * 在给定的日期加上或减去指定月份后的日期
     *
     * @param sourceDate 原始时间
     * @param month      要调整的月份，向前为负数，向后为正数
     * @return
     */
    public static Date stepMonth(Date sourceDate, int month) {
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.MONTH, month);

        return c.getTime();
    }

    /**
     * 在给定的日期加上或减去指定月份后的日期
     *
     * @param sourceDate 原始时间
     * @param month      要调整的月份，向前为负数，向后为正数
     * @return
     */
    public static String stepMonth(Date sourceDate, int month, String format) {
        if (format == null) format = DTF_YMD;
        return date2String(stepMonth(sourceDate, month), format);
    }

    /**
     * 在给定的日期加上或减去指定小时后的日期
     *
     * @param sourceDate 原始时间
     * @param hours      要调整的小时，向前为负数，向后为正数
     * @return
     */
    public static String stepHours(String sourceDate, int hours) {
        Date date = string2Date(sourceDate, DTF_YMD_HMS);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, hours);

        return date2String(c.getTime(), DTF_YMD_HMS);
    }

    /**
     * 获取当前时间基础的前\后时间段的日期
     *
     * @param field  天\时\分\秒
     * @param amount 值
     * @return
     */
    public static Date getSpecialDate(Date date, int field, int amount) {
        if (date == null) {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    public static Date getFirstDayfOMonth() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//1:本月第一天
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    public static String date2String(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        if (Strings.isBlank(pattern)) {
            pattern = DTF_YMD_HMS;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static Long subtractionDate(Long startTime) {
        startTime = startTime / 1000;
        LocalDateTime ldt1 = LocalDateTime.ofEpochSecond(startTime, 0, ZoneOffset.ofHours(8));
        LocalDateTime ldt2 = LocalDateTime.now();
        Duration duration = Duration.between(ldt1, ldt2);
        return duration.toDays();
    }

    /**
     * 计算字符串时间相差的天数/小时数/分钟数
     *
     * @param startDate 起始时间
     * @param endDate   结束时间
     * @param idStr        标识（day：表示天,hour:表示小时，minute：表示分钟）
     * @return
     */
    public static long subtractionDate(Date startDate, Date endDate, String idStr) {
        LocalDateTime endLocalDateTime = LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime startLocalDateTime = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
        Duration duration = Duration.between(endLocalDateTime, startLocalDateTime);
        long value = 0;
        switch (idStr) {
            case DAY:
                value = duration.toDays();
                break;
            case HOURS:
                value = duration.toHours();
                break;
            case MINUTE:
                value = duration.toMinutes();
                break;
        }
        return value;
    }

    /**
     * 比较两个时间字符大小
     *
     * @return
     */
    public static int compareDateStr(String preTime, String nextTime) {
        return LocalDateTime.parse(preTime, DateTimeFormatter.ofPattern(DTF_YMD_HMS)).
                compareTo(LocalDateTime.parse(nextTime, DateTimeFormatter.ofPattern(DTF_YMD_HMS)));

    }

    public static String transferTimeStr(String sourceTime) {
        LocalDate endLocalDate = LocalDate.parse(sourceTime, DateTimeFormatter.ofPattern(DateUtil.DTF_YMD));
        return endLocalDate.atStartOfDay().format(DateTimeFormatter.ofPattern(DateUtil.DTF_YMD_HMS));
    }


    /**
     * 获取某个时间的小时
     *
     * @param time
     * @return
     */
    public static Integer getHour(Date time) {
		/*if (time == null) {
			return null;
		}*/
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static String getResentYearTimeRange() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(calendar1.get(Calendar.YEAR) - 1, calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        Date beginOfDate = calendar1.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        Date endOfDate = calendar2.getTime();
        SimpleDateFormat format = new SimpleDateFormat(DTF_YMD_HMS);
        return format.format(beginOfDate) + "~" + format.format(endOfDate);
    }

    public static String getTodayTimeRange() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        Date beginOfDate = calendar1.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH),
                23, 59, 59);
        Date endOfDate = calendar2.getTime();
        SimpleDateFormat format = new SimpleDateFormat(DTF_YMD_HMS);
        return format.format(beginOfDate) + "~" + format.format(endOfDate);
    }

    public static String getTodayTimeRange(String endTime) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        Date beginOfDate = calendar1.getTime();

        Calendar calendar2 = Calendar.getInstance();
        if (StringUtils.isEmpty(endTime)) {
            calendar2.set(calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH),
                    23, 59, 59);
        } else {
            calendar2.setTime(string2Date(endTime, DTF_YMD_HMS));
        }
        Date endOfDate = calendar2.getTime();
        SimpleDateFormat format = new SimpleDateFormat(DTF_YMD_HMS);
        return format.format(beginOfDate) + "~" + format.format(endOfDate);
    }

    //获取今天零点时间
    public static Date getTodayZeroTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        Date endDate = calendar1.getTime();
        return endDate;
    }


    /**
     * 获取昨天零点时间
     * @return
     */
    public static String getYesterdayZeroTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        calendar1.add(Calendar.DATE, -1);
        SimpleDateFormat format = new SimpleDateFormat(DTF_YMD_HMS);
        return format.format(calendar1.getTime());
    }

    /**
     * 获取昨天23：59:59时间
     * @return
     */
    public static String getYesterdayEndTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH),
                23, 59, 59);
        calendar1.add(Calendar.DATE, -1);
        SimpleDateFormat format = new SimpleDateFormat(DTF_YMD_HMS);
        return format.format(calendar1.getTime());
    }

    /**
     * 判断日期是否合法
     *
     * @param str
     * @return
     */
    public static boolean isValidDate(String str, String format) {
        boolean convertSuccess = true;
        if (Strings.isBlank(format)) {
            format = DTF_YMD;
        }
        if (Strings.isBlank(str) || str.length() != format.length()) {
            convertSuccess = false;
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            simpleDateFormat.setLenient(false);
            try {
                simpleDateFormat.parse(str);
            } catch (ParseException e) {
                convertSuccess = false;
            }
        }

        return convertSuccess;
    }

    /**
     * 根据localDate获取几周前的星期一日期
     *
     * @param localDate
     * @param delay
     * @return
     */
    public static String getStartWeekDate(LocalDate localDate, int delay) {
        localDate = localDate.plusWeeks(delay);
        TemporalAdjuster FIRST_OF_WEEK = TemporalAdjusters.ofDateAdjuster(date -> date.minusDays(date.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue()));
        LocalDate startFirstWeek = localDate.with(FIRST_OF_WEEK);  //开始周开始日期
        return startFirstWeek.format(DTF_YMD_FORMAT);
    }

    /**
     * 根据localDate获取几周前的星期天日期
     *
     * @param localDate
     * @param delay
     * @return
     */
    public static String getEndWeekDate(LocalDate localDate, int delay) {
        localDate = localDate.plusWeeks(delay);
        TemporalAdjuster LAST_OF_WEEK = TemporalAdjusters.ofDateAdjuster(date -> date.plusDays(DayOfWeek.SUNDAY.getValue() - date.getDayOfWeek().getValue()));
        LocalDate endFirstWeek = localDate.with(LAST_OF_WEEK);     //开始周结束日期
        return endFirstWeek.format(DTF_YMD_FORMAT);
    }

    public static Date getOneWeekAgo(Date date) {
        // 创建 Calendar 对象并设置为当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 减去一周的时间
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        // 获取一周前的时间
        Date oneWeekAgo = calendar.getTime();
        return oneWeekAgo;
    }

    public static Date getOneMonthAgo(Date date) {
        // 创建 Calendar 对象并设置为当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 减去一个月的时间
        calendar.add(Calendar.MONTH, -1);
        // 获取一个月前的时间
        Date oneMonthAgo = calendar.getTime();
        return oneMonthAgo;
    }

    public static String getDayOfWeek(Date date) {
        // 使用 SimpleDateFormat 格式化日期并获取星期几
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        SimpleDateFormat dateFormatYYYY = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        String dayOfWeek = dateFormat.format(date);
        String dayOfWeekWithoutName = dayOfWeek.replaceAll("星期", "");
        log.info("dateFormatYYYY={}, dayOfWeekWithoutName={}", dateFormatYYYY.format(date), dayOfWeekWithoutName);
        return dayOfWeekWithoutName;
    }

    /**
     * 获取两个日期中间的所有天数(yyyy-MM-dd HH:mm:ss形式)
     *
     * @param startTime
     * @param endTime
     * @param pattern   时间格式（eg:"yyyy-MM-dd HH:mm:ss"）
     * @param idStr     标识（day：表示天,hour:表示小时，minute：表示分钟）
     * @return
     */
    public static List<String> getBetweenTime(String startTime, String endTime, String pattern, String idStr) {
        List<String> list = new ArrayList<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime startDateTime = LocalDateTime.parse(startTime, df);
        LocalDateTime endDateTime = LocalDateTime.parse(endTime, df);
        long distance = 0L;
        switch (idStr) {
            case DAY:
                distance = ChronoUnit.DAYS.between(startDateTime, endDateTime);
                if (distance < 1) {
                    if (startTime.equals(startTime)) {
                        list.add(startTime);
                    }
                    return list;
                }
                Stream.iterate(startDateTime, d -> {
                    return d.plusDays(1);
                }).limit(distance + 1).forEach(f -> {
                    list.add(f.format(df));
                });
                break;
            case HOURS:
                distance = ChronoUnit.HOURS.between(startDateTime, endDateTime);
                if (distance < 1) {
                    if (startTime.equals(startTime)) {
                        list.add(startTime);
                    }
                    return list;
                }
                Stream.iterate(startDateTime, d -> {
                    return d.plusHours(1);
                }).limit(distance + 1).forEach(f -> {
                    list.add(f.format(df));
                });
                break;

        }

        return list;
    }

    public static Date string2Date(String date, String pattern) {
        try {
            if (date == null) {
                return null;
            }
            if (Strings.isBlank(pattern)) {
                pattern = DTF_YMD_HMS;
            }
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(date);
        } catch (ParseException e) {
            log.error("时间转换异常1");
        }
        return null;
    }


    public static LocalDateTime string2LocalDateTime(String date, String pattern) {

        if (Strings.isBlank(pattern)) {
            pattern = DTF_YMD_HMS;
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(date, df);

    }

    /**
     * 日期加减
     * @param dateTime
     * @param days
     * @return
     */
    public static LocalDateTime addDays(LocalDateTime dateTime, int days) {
        return dateTime.plusDays(days);
    }

    /**
     * 获取日期 00:00:00
     * @param dateTime
     * @return
     */
    public static LocalDateTime getStartDateTime(LocalDateTime dateTime) {
        return LocalDateTime.of(dateTime.getYear(),dateTime.getMonth(),dateTime.getDayOfMonth(),0,0,0);
    }

    /**
     * 获取日期23：59:59
     * @param dateTime
     * @return
     */
    public static LocalDateTime getEndDateTime(LocalDateTime dateTime) {
        return LocalDateTime.of(dateTime.getYear(),dateTime.getMonth(),dateTime.getDayOfMonth(),23,59,59);

    }

    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.now();

        System.out.println("getStartDateTime(localDateTime) = " + getEndDateTime(localDateTime));
    }

    public static int compareDateStr(String preTime, String nextTime, String format) {
        try {
            SimpleDateFormat dFormat = new SimpleDateFormat(format);
            Date date1 = dFormat.parse(preTime);
            Date date2 = dFormat.parse(nextTime);
            return date1.compareTo(date2);
        } catch (ParseException e) {
            log.error("时间转换异常");
        }
        return -100;
    }

    public static Integer getMonth(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static Integer getWeek(Date date) {
        // 创建一个 Calendar 对象，并设置其时间为要查询的日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 获取日期所在的周数
        int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
        return weekNumber;
    }

    public static Integer getDay(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Integer getCurrentWeek() {
        LocalDate currentDate = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        Integer currentWeek = currentDate.get(weekFields.weekOfWeekBasedYear());
        return currentWeek;
    }

    public static Integer getWeekByDate(Date date) {
        Instant instant = date.toInstant();
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        Integer currentWeek = localDate.get(weekFields.weekOfWeekBasedYear());
        return currentWeek;
    }

    public static String getThisYearTimeRange() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.YEAR, -1);
        Date beginOfDate = calendar1.getTime();

        Calendar calendar2 = Calendar.getInstance();
        Date endOfDate = calendar2.getTime();
        SimpleDateFormat format = new SimpleDateFormat(DTF_YMD_HMS);
        return format.format(beginOfDate) + "~" + format.format(endOfDate);
    }

    public static Date getWeekAgoDay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - 7);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    public static String getThisMonthTimeRange() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_MONTH, -30);
        Date beginOfDate = calendar1.getTime();

        Calendar calendar2 = Calendar.getInstance();
        Date endOfDate = calendar2.getTime();
        SimpleDateFormat format = new SimpleDateFormat(DTF_YMD_HMS);
        return format.format(beginOfDate) + "~" + format.format(endOfDate);
    }

    /**
     * 将字符串转日期成Long类型的时间戳，格式为：yyyy-MM-dd HH:mm:ss
     */
    public static Long convertTimeToLong(String time) {
        Assert.notNull(time, "time is null");
        return LocalDateTime
                .from(LocalDateTime.parse(time, DateTimeFormatter.ofPattern(DTF_YMD_HMS)))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    public static LocalDate getToday() {
        LocalDate date = LocalDate.now();
        return date;
    }

    /**
     * 得到几天前的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 得到几天后的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    /**
     * 获取两个日期中间的所有日期
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<String> getBetweenDates(Date startTime, Date endTime) {
        // 返回的日期集合
        List<String> dates = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat(DTF_YMD);

        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(startTime);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(endTime);
        while (tempStart.before(tempEnd)) {
            dates.add(dateFormat.format(tempStart.getTime()));
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }

        return dates;
    }


    public static Long strToTimeStamp(String date) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.parse(date).getTime();
    }
    public static Date getDateStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    public static Date getDateEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    public static String getTime(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date datestr = (Date) sdf.parse(date);
            String formatStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(datestr);
            return formatStr;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = format.format(today);
        return result;
    }

    public static String getTimeStr(String time) {
        return (time.startsWith("0")) ? time.substring(1, 2) : time.substring(0, 2);
    }

    public static String getMinuteBeforeStr(int minuteNum) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = format.format(System.currentTimeMillis() - 1000 * minuteNum * 60);
        return result;
    }

    public static boolean validate(String date) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        if (null == date) {
            return false;
        } else {
            try {
                sdf1.parse(date);
                return true;
            } catch (ParseException e) {
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
                try {
                    sdf2.parse(date);
                    return true;
                } catch (ParseException parseException) {
                    return false;
                }
            }
        }
    }

    /**
     * 获取年月
     *
     * @param date
     * @return
     */
    public static String getYearMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Date today = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String result = format.format(today);
        return result;
    }

    /**
     * 获取年
     *
     * @return
     */
    public static String getCurrentYear() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Date today = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        String result = format.format(today);
        return result;
    }

    /**
     * 获取年
     *
     * @return
     */
    public static String getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Date today = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        String result = format.format(today);
        return result;
    }

    public static String getHourMinute(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Date today = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String result = format.format(today);
        return result;
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 获取月日
     *
     * @param dt
     * @return 获取月日
     */
    public static String getDateString(Date dt, String spit) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        String date = null;
        if (spit == null){
            date = (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日";
        }else {
            date = (cal.get(Calendar.MONTH) + 1) + spit + cal.get(Calendar.DAY_OF_MONTH);
        }

        return date;
    }

    public static long localDateTimeToLong(LocalDateTime dateTime) {
        Timestamp timestamp = Timestamp.valueOf(dateTime);
        return timestamp.getTime();
    }

    /**
     * 判断两个日期是同一天
     *
     * @param date1
     * @param date2
     * @return 判断两个日期是同一天
     */
    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        String date = null;

        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 在给定的日期加上或减去指定天数后的日期
     *
     * @param sourceDate 原始时间
     * @param day      要调整的日期，向前为负数，向后为正数
     * @return
     */
    public static Date stepDay(Date sourceDate, int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.DAY_OF_YEAR, day);
        return c.getTime();
    }

}
