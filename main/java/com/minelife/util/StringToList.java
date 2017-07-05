package com.minelife.util;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by colby on 1/7/2017.
 */
public abstract class StringToList<E> {

    private String[] array;
    private List<E> list = Lists.newArrayList();

    public StringToList(String s) {
        array = ArrayUtil.fromString(s);
    }

    public StringToList<E> run() {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null && !array[i].isEmpty())
                if (parse(array[i]) != null)
                    list.add(parse(array[i]));
        }

        return this;
    }

    public List<E> getList() {
        return list;
    }

    public abstract E parse(String s);

}
