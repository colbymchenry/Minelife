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
        for(E e : list) strings.add(toString(e));

        return strings;
    }

    public String getListAsString() {
        strings.clear();
        List<String> stringList = getList();
        return ArrayUtil.toString(stringList.toArray(new String[stringList.size()]));
    }

    public abstract String toString(E o);

    public String getString() {
        return ArrayUtil.toString(strings.toArray(new String[strings.size()]));
    }
}
