package com.minelife.gangs.network;

import com.minelife.Minelife;
import com.minelife.gangs.Gang;
import com.minelife.gangs.ModGangs;
import com.minelife.util.PlayerHelper;
import com.minelife.util.client.PacketPopupMessage;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Set;
import java.util.UUID;

public class PacketModifyPlayer implements IMessage {

    private UUID playerUUID, gangUUID;
    private boolean kick, setMember, setOfficer, setLeader;

    public PacketModifyPlayer() {
    }

    public PacketModifyPlayer(Gang gang, UUID playerUUID, boolean kick, boolean setMember, boolean setOfficer, boolean setLeader) {
        this.gangUUID = gang.getGangID();
        this.playerUUID = playerUUID;
        this.kick = kick;
        this.setMember = setMember;
        this.setOfficer = setOfficer;
        this.setLeader = setLeader;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gangUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        playerUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        kick = buf.readBoolean();
        setMember = buf.readBoolean();
        setOfficer = buf.readBoolean();
        setLeader = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, gangUUID.toString());
        ByteBufUtils.writeUTF8String(buf, playerUUID.toString());
        buf.writeBoolean(kick);
        buf.writeBoolean(setMember);
        buf.writeBoolean(setOfficer);
        buf.writeBoolean(setLeader);
    }

    public static class Handler implements IMessageHandler<PacketModifyPlayer, IMessage> {

        @Override
        public IMessage onMessage(PacketModifyPlayer message, MessageContext ctx) {
            EntityPlayerMP playerSender = ctx.getServerHandler().playerEntity;
            EntityPlayerMP playerReceiver = PlayerHelper.getPlayer(message.playerUUID);

            Gang gang = ModGangs.getPlayerGang(playerSender.getUniqueID());

            if (gang == null) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("You do not belong to a gang."), playerSender);
                return null;
            }

            boolean isLeader = gang.getLeader() != null && gang.getLeader().equals(playerSender.getUniqueID());

            Set<UUID> officers = gang.getOfficers();
            Set<UUID> members = gang.getMembers();

            if (isLeader && message.setLeader) {
                officers.add(gang.getLeader());
                gang.setLeader(message.playerUUID);
                officers.remove(message.playerUUID);
                members.remove(message.playerUUID);
                gang.setOfficers(officers);
                gang.setMembers(members);

                if (playerReceiver != null)
                    playerReceiver.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You are now the leader of the gang!"));
                return null;
            } else if (!isLeader && message.setLeader) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("You do not have permission to do that."), playerSender);
                return null;
            }


            if (message.setOfficer || message.setMember) {
                if (!isLeader) {
                    Minelife.NETWORK.sendTo(new PacketPopupMessage("You do not have permission to do that."), playerSender);
                    return null;
                }

                if(message.playerUUID.equals(gang.getLeader())) {
                    Minelife.NETWORK.sendTo(new PacketPopupMessage("You must make another member a leader."), playerSender);
                    return null;
                }


                if (message.setOfficer) {
                    members.remove(message.playerUUID);
                    officers.add(message.playerUUID);
                    if (playerReceiver != null)
                        playerReceiver.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You are now an officer of the gang!"));
                } else if (message.setMember) {
                    officers.remove(message.playerUUID);
                    members.add(message.playerUUID);
                    if (playerReceiver != null)
                        playerReceiver.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You are now a member of the gang..."));
                }

                gang.setOfficers(officers);
                gang.setMembers(members);
            }

            if (message.kick) {

                if(message.playerUUID.equals(gang.getLeader())) {
                    Minelife.NETWORK.sendTo(new PacketPopupMessage("You cannot kick the leader."), playerSender);
                    return null;
                }

                if (gang.getOfficers().contains(playerSender.getUniqueID()) && gang.getOfficers().contains(message.playerUUID)) {
                    Minelife.NETWORK.sendTo(new PacketPopupMessage("An officer cannot kick another officer."), playerSender);
                    return null;
                }

                if (!gang.getOfficers().contains(playerSender.getUniqueID()) && !isLeader) {
                    Minelife.NETWORK.sendTo(new PacketPopupMessage("You do not have permission to do that."), playerSender);
                    return null;
                }

                officers.remove(message.playerUUID);
                members.remove(message.playerUUID);

                gang.setOfficers(officers);
                gang.setMembers(members);

                if (playerReceiver != null)
                    playerReceiver.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You have been kicked from the gang..."));

            }

            gang.sendPacketToAll(new PacketModifyPlayerResponse(message.playerUUID, message.kick, message.setMember, message.setOfficer, message.setLeader));

            return null;
        }
    }

}
