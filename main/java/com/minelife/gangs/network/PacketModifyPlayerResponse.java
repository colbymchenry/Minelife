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

public class PacketModifyPlayerResponse implements IMessage {

    private UUID playerUUID;
    private boolean kick, setMember, setOfficer, setLeader;

    public PacketModifyPlayerResponse() {
    }

    public PacketModifyPlayerResponse(UUID playerUUID, boolean kick, boolean setMember, boolean setOfficer, boolean setLeader) {
        this.kick = kick;
        this.setMember = setMember;
        this.setOfficer = setOfficer;
        this.setLeader = setLeader;
        this.playerUUID = playerUUID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        kick = buf.readBoolean();
        setMember = buf.readBoolean();
        setOfficer = buf.readBoolean();
        setLeader = buf.readBoolean();
        playerUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(kick);
        buf.writeBoolean(setMember);
        buf.writeBoolean(setOfficer);
        buf.writeBoolean(setLeader);
        ByteBufUtils.writeUTF8String(buf, playerUUID.toString());
    }

    public static class Handler implements IMessageHandler<PacketModifyPlayerResponse, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketModifyPlayerResponse message, MessageContext ctx) {
            if(Minecraft.getMinecraft().currentScreen instanceof GuiGangMenu) {
                GuiGangMenu gangMenu = (GuiGangMenu) Minecraft.getMinecraft().currentScreen;

                if(message.kick) {
                    System.out.println("CALLED");
                    gangMenu.selectedMember = null;
                    gangMenu.members.remove(message.playerUUID);
                    gangMenu.GuiMembersList.names.remove(message.playerUUID);
                    gangMenu.Gang.members.remove(message.playerUUID);
                    gangMenu.Gang.officers.remove(message.playerUUID);
                }

                if(message.setOfficer) {
                    System.out.println("CALLED1");
                    gangMenu.selectedMember = null;
                    gangMenu.Gang.members.remove(message.playerUUID);
                    gangMenu.Gang.officers.add(message.playerUUID);
                }

                if(message.setMember) {
                    gangMenu.selectedMember = null;
                    gangMenu.Gang.officers.remove(message.playerUUID);
                    gangMenu.Gang.members.add(message.playerUUID);
                }
            }
            return null;
        }

    }

}
