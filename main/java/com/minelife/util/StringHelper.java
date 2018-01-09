package com.minelife.util;

public class StringHelper {

    public static String ParseFormatting(String s, char c) {
       return s.replaceAll("" + c, String.valueOf('\u00a7'));
    }

}
