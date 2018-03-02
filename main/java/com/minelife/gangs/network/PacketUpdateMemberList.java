package com.minelife.gangs.network;

import com.minelife.gangs.client.gui.GuiGangMenu;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.util.UUID;

public class PacketUpdateMemberList implements IMessage {

    private UUID playerUUID;
    private String playerName;

    public PacketUpdateMemberList() {
    }

    public PacketUpdateMemberList(UUID playerUUID, String playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        playerName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerUUID.toString());
        ByteBufUtils.writeUTF8String(buf, playerName);
    }

    public static class Handler implements IMessageHandler<PacketUpdateMemberList, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketUpdateMemberList message, MessageContext ctx) {
            if(Minecraft.getMinecraft().currentScreen instanceof GuiGangMenu) {
                GuiGangMenu gangMenu = (GuiGangMenu) Minecraft.getMinecraft().currentScreen;
                gangMenu.members.put(message.playerUUID, message.playerName);
                gangMenu.GuiMembersList.names.put(message.playerUUID, message.playerName);
                gangMenu.Gang.members.add(message.playerUUID);
            }
            return null;
        }

    }
}
