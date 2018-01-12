package com.minelife.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    private static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");


    public static String DateToString(Date date) {
        return df.format(date);
    }

    public static Date StringToDate(String str) {
        try {
            return df.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

}
