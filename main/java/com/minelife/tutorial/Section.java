package com.minelife.tutorial;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.util.List;

public class Section implements Comparable<Section>{

    public String name;
    public List<Page> pages = Lists.newArrayList();

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
