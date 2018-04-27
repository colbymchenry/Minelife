package com.minelife.tutorial;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.util.LinkedList;

public class Page implements Comparable<Page>{

    public int pageNumber;
    public LinkedList<String> lines = Lists.newLinkedList();

    public Page(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(pageNumber);
        buf.writeInt(lines.size());
        lines.forEach(line -> ByteBufUtils.writeUTF8String(buf, line));
    }

    public static Page fromBytes(ByteBuf buf) {
        Page page = new Page(buf.readInt());
        int lines = buf.readInt();
        for (int i = 0; i < lines; i++) page.lines.add(ByteBufUtils.readUTF8String(buf));
        return page;
    }

    @Override
    public int compareTo(Page o) {
        return pageNumber - o.pageNumber;
    }
}
