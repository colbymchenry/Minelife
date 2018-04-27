package com.minelife.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {

    public static String ParseFormatting(String s, char c) {
       return s.replaceAll("" + c, String.valueOf('\u00a7'));
    }

    public static boolean containsSpecialChar(String s){
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s);
        return m.find();
    }

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String randomAlphaNumeric(int count) {

        StringBuilder builder = new StringBuilder();

        while (count-- != 0) {

            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());

            builder.append(ALPHA_NUMERIC_STRING.charAt(character));

        }

        return builder.toString();

    }
}
