package com.minelife.util;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArrayUtil {

    public static String toString(String[] objects) {
        return Arrays.toString(objects);
    }

    public static String[] fromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        String result[] = new String[strings.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = strings[i];
        }
        return result;
    }

    public static void cleanNull(List list) {
        list.removeAll(Collections.singleton(null));
    }


    public static List<String> toStringList(List<?> objects) {
        List<String> list = Lists.newArrayList();
        objects.forEach(o -> list.add(o.toString()));
        return list;
    }


}
