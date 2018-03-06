package com.minelife.tutorial;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import javafx.collections.transformation.SortedList;

import java.util.List;
import java.util.Set;

public class Section implements Comparable<Section>{

    public String name;
    public Set<Page> pages = Sets.newTreeSet();

    public Section(String name) {
        this.name = name;
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeInt(pages.size());
        pages.forEach(page -> page.toBytes(buf));
    }

    public static Section fromBytes(ByteBuf buf) {
        Section section = new Section(ByteBufUtils.readUTF8String(buf));
        int pagesSize = buf.readInt();
        for (int i = 0; i < pagesSize; i++) section.pages.add(Page.fromBytes(buf));
        return section;
    }

    @Override
    public int compareTo(Section o) {
        return o.name.compareTo(name);
    }
}
