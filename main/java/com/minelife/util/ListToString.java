package com.minelife.util;

import com.google.common.collect.Lists;

import java.util.List;

public abstract class ListToString<E> {

    private List<String> strings = Lists.newArrayList();
    private List<E> list;

    public ListToString(List<E> list) {
        this.list = list;
    }

    public List<String> getList() {
        strings.clear();

        for(E e : list) strings.add(toString(e));

        return strings;
    }

    public String getListAsString() {
        List<String> stringList = getList();
        return ArrayUtil.toString(stringList.toArray(new String[stringList.size()]));
    }

    public abstract String toString(E o);

    public String getString() {
        List<String> strings = getList();
        return ArrayUtil.toString(strings.toArray(new String[strings.size()]));
    }
}
