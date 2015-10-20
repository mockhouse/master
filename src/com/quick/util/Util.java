package com.quick.util;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by eropate on 20/6/15.
 */
public abstract class Util {


    public static final String OPEN_BRACKET = "(";
    public static final String CLOSE_BRACKET = ")";
    public static boolean isNull(Object obj) {return obj == null;}
    public static boolean isNullOrBlank(String str){return isNull(str) || "".equals(str.trim());}
    public static boolean isNullString(String str){return isNull(str) || "null".equalsIgnoreCase(str.trim());}
    public static boolean isNegative(Long number) {return !isNull(number) && number < 0;}
    public static boolean isPositive(Long number) {return !isNull(number) && number > 0;}
    public static boolean isNullOrEmpty(List lst) { return lst == null || lst.isEmpty();}
    public static long timeInSeconds(long time,TimeUnit source) {
        return TimeUnit.SECONDS.convert(time,source);
    }

    public static String timeInSecondsAsString(long time,TimeUnit source) {
        return TimeUnit.SECONDS.convert(time,source) + " s";
    }
    public static String timeInMinutesAsString(long time,TimeUnit source) {
        return TimeUnit.MINUTES.convert(time,source) + " m";
    }

    public static String timeAsString(long time,TimeUnit source) {
        long secs = TimeUnit.SECONDS.convert(time,source);
        if(secs < 60)
            return timeInSecondsAsString(time,source);
        if(secs < 3600)
            return timeInMinutesAsString(time,source);
        return TimeUnit.HOURS.convert(time,source) + " h";
    }

    public static String getOnlyDateFromLong(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String getOnlyTimeFromLong(long time) {
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        return formatter.format(date);
    }
}
