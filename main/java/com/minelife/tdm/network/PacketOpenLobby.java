package com.minelife.tdm.network;

import com.google.common.collect.Lists;
import com.minelife.guns.item.EnumGun;
import com.minelife.tdm.Match;
import com.minelife.tdm.client.gui.GuiLobby;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class PacketOpenLobby implements IMessage {

    private Match match;
    private String arena;
    private List<EnumGun> gunSkins;

    public PacketOpenLobby() {
    }

    public PacketOpenLobby(Match match, String arena, List<EnumGun> gunSkins) {
        this.match = match;
        this.arena = arena;
        this.gunSkins = gunSkins;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        match = Match.fromBytes(buf);
        arena = ByteBufUtils.readUTF8String(buf);
        int skinsAvailable = buf.readInt();
        gunSkins = Lists.newArrayList();
        for (int i = 0; i < skinsAvailable; i++) gunSkins.add(EnumGun.values()[buf.readInt()]);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        match.toBytes(buf);
        ByteBufUtils.writeUTF8String(buf, arena);
        buf.writeInt(gunSkins.size());
        gunSkins.forEach(gunSkin -> buf.writeInt(gunSkin.ordinal()));
    }

    public static class Handler implements IMessageHandler<PacketOpenLobby, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenLobby message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiLobby(message.match, message.arena, message.gunSkins)));
            return null;
        }

    }

}
