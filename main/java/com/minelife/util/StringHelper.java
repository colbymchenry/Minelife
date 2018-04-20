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
}
