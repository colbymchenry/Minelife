package com.minelife.tutorial;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.util.LinkedList;

public class Page {

    public LinkedList<String> lines = Lists.newLinkedList();

    public void toBytes(ByteBuf buf) {
        buf.writeInt(lines.size());
        lines.forEach(line -> ByteBufUtils.writeUTF8String(buf, line));
    }

    public static Page fromBytes(ByteBuf buf) {
        Page page = new Page();
        int lines = buf.readInt();
        for (int i = 0; i < lines; i++) page.lines.add(ByteBufUtils.readUTF8String(buf));
        return page;
    }

}
