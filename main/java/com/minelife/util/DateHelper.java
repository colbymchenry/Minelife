package com.minelife.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    private static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    public static String dateToString(Date date) {
        return df.format(date);
    }

    public static Date stringToDate(String str) {
        try {
            return df.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static long getDiffSeconds(Date startDate, Date endDate) {
        long diffInMilliSec = endDate.getTime() - startDate.getTime();
        return (diffInMilliSec / 1000) % 60;
    }

    public static long getDiffMinutes(Date startDate, Date endDate) {
        long diffInMilliSec = endDate.getTime() - startDate.getTime();
        return (diffInMilliSec / (1000 * 60)) % 60;
    }

    public static long getDiffHours(Date startDate, Date endDate) {
        long diffInMilliSec = endDate.getTime() - startDate.getTime();
        return (diffInMilliSec / (1000 * 60 * 60)) % 24;
    }

    public static long getDiffDays(Date startDate, Date endDate) {
        long diffInMilliSec = endDate.getTime() - startDate.getTime();
        return (diffInMilliSec / (1000 * 60 * 60 * 24)) % 365;
    }

    public static long getDiffYears(Date startDate, Date endDate) {
        long diffInMilliSec = endDate.getTime() - startDate.getTime();
        return (diffInMilliSec / (1000l * 60 * 60 * 24 * 365));
    }

}
