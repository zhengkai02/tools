package com.zk.utools.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xh on 2019/3/19.
 */
public abstract class DateUtil {
    private DateUtil() {
    }

    /**
     * 日期时分秒格式
     */
    public static final String DATETIME_FORMAT = "yyyyMMddHHmmss";

    /**
     * 日期格式
     */
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    /**
     * 日期格式
     */
    public static final String DATE_FORMAT_1 = "yyyy-MM-dd";


    /**
     * 日期时分秒毫秒格式
     */
    public static final String DATETIME_FORMAT_1 = "yyyy-MM-dd HH:mm:ss.SSS";


    /**
     * 日期时分秒毫秒格式
     */
    public static final String DATETIME_FORMAT_2 = "yyyyMMddHHmmssSSS";


    /**
     * 日期时分秒格式
     */
    public static final String DATETIME_FORMAT_3 = "yyyy-MM-dd HH:mm:ss";

    /**
     * 得到SimpleDateFormat实例
     *
     * @param format 格式
     * @return SimpleDateFormat
     */
    private static SimpleDateFormat getDateFormat(String format) {
        return new SimpleDateFormat(format);
    }

    /**
     * 字符串格式化成日期
     *
     * @param date 日期
     * @return 日期
     */
    public static Date formatDate(String date) {
        return formatDate(date, DateUtil.DATETIME_FORMAT);
    }

    /**
     * 字符串格式化成日期(带格式)
     *
     * @param date   日期
     * @param format 格式
     * @return 日期
     */
    public static Date formatDate(String date, String format) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }

        SimpleDateFormat simpleDateFormat = getDateFormat(format);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("the date string " + date + " is not matching format", e);
        }
    }


    /**
     * 日期格式化成字符串
     *
     * @param date   Date
     * @param format String
     * @return String
     */
    public static String formatString(Date date, String format) {
        if (date == null) {
            return null;
        } else {
            SimpleDateFormat sdf = getDateFormat(format);
            return sdf.format(date);
        }
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @param format
     * @return
     */
    public static String timeStamp2Date(long seconds, String format) {
        SimpleDateFormat dateformat;
        if (null == format || StringUtils.isNotEmpty(format)) {
            dateformat = new SimpleDateFormat(DATETIME_FORMAT_3);
        } else {
            dateformat = new SimpleDateFormat(format);
        }
        String dateStr = dateformat.format(seconds);
        return dateStr;
    }

}
