package com.minelife.tdm.network;

import com.google.common.collect.Maps;
import com.minelife.tdm.Match;
import com.minelife.tdm.client.gui.GuiMatchSearch;
import com.minelife.util.NumberConversions;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

public class PacketOpenMatchSearch implements IMessage {

    private Map<String, Match> arenas;
    private Map<String, String> arenaPixels;

    public PacketOpenMatchSearch() {
    }

    public PacketOpenMatchSearch(Map<String, Match> arenas, Map<String, String> arenaPixels) {
        this.arenas = arenas;
        this.arenaPixels = arenaPixels;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int arenaSize = buf.readInt();
        arenas = Maps.newHashMap();
        for (int i = 0; i < arenaSize; i++) {
            String arenaName = ByteBufUtils.readUTF8String(buf);
            Match match = null;
            if (buf.readBoolean()) match = Match.fromBytes(buf);
            arenas.put(arenaName, match);
        }

        int arenaPixelsSize = buf.readInt();
        arenaPixels = Maps.newHashMap();
        for (int i = 0; i < arenaPixelsSize; i++) {
            String arenaName = ByteBufUtils.readUTF8String(buf);
            StringBuilder pixels = new StringBuilder();
            if (buf.readBoolean()) {
                int pixelLength = buf.readInt();
                for (int i1 = 0; i1 < pixelLength; i1++) {
                    pixels.append(buf.readInt()).append(",").append(buf.readInt()).append(",").append(buf.readInt()).append(";");
                }
            }
            arenaPixels.put(arenaName, pixels.toString().isEmpty() ? null : pixels.toString());
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(arenas.size());
        arenas.forEach((arenaName, match) -> {
            ByteBufUtils.writeUTF8String(buf, arenaName);
            buf.writeBoolean(match != null);
            if (match != null) match.toBytes(buf);
        });

        buf.writeInt(arenaPixels.size());
        arenaPixels.forEach((arenaName, pixels) -> {
            ByteBufUtils.writeUTF8String(buf, arenaName);
            buf.writeBoolean(pixels != null);
            if (pixels != null) {
                buf.writeInt(pixels.split(";").length);
                for (String s : pixels.split(";")) {
                    if (!s.isEmpty()) {
                        int x = NumberConversions.toInt(s.split(",")[0]);
                        int y = NumberConversions.toInt(s.split(",")[1]);
                        int color = NumberConversions.toInt(s.split(",")[2]);
                        buf.writeInt(x);
                        buf.writeInt(y);
                        buf.writeInt(color);
                    }
                }
            }
        });
    }

    public static class Handler implements IMessageHandler<PacketOpenMatchSearch, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenMatchSearch message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiMatchSearch(message.arenas, message.arenaPixels)));
            return null;
        }
    }

}
