package com.minelife.gangs.network;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.gangs.Gang;
import com.minelife.gangs.GangPermission;
import com.minelife.gangs.GangRole;
import com.minelife.notifications.Notification;
import com.minelife.notifications.NotificationType;
import com.minelife.util.PlayerHelper;
import com.minelife.util.client.PacketPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class PacketRemoveMember implements IMessage {

    private UUID playerID;

    public PacketRemoveMember() {
    }

    public PacketRemoveMember(UUID playerID) {
        this.playerID = playerID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerID.toString());
    }

    public static class Handler implements IMessageHandler<PacketRemoveMember, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketRemoveMember message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Gang playerGang = Gang.getGangForPlayer(player.getUniqueID());

            if (playerGang == null) {
                PacketPopup.sendPopup("You do not belong to a gang.", player);
                return null;
            }

            if (!playerGang.hasPermission(player.getUniqueID(), GangPermission.KICK_MEMBERS)) {
                PacketPopup.sendPopup("You do not have permission to kick members", player);
                return null;
            }

            if (playerGang.getMembers().containsKey(message.playerID) && playerGang.getMembers().get(message.playerID) == GangRole.OLDER &&
                    !playerGang.getOwner().equals(player.getUniqueID())) {
                PacketPopup.sendPopup("Only the owner can kick an older.", player);
                return null;
            }

            Map<UUID, GangRole> members = playerGang.getMembers();
            members.remove(message.playerID);
            playerGang.setMembers(members);
            playerGang.writeToDatabase();

            Minelife.getNetwork().sendTo(new PacketOpenGangGui(playerGang), player);

            playerGang.getMembers().keySet().forEach(playerID -> {
                if (PlayerHelper.getPlayer(playerID) != null) {
                    PacketSendGangMembers.sendMembers(playerGang, PlayerHelper.getPlayer(playerID));
               }
            });

            PacketSendGangMembers.sendMembers(playerGang, player);

            if(PlayerHelper.getPlayer(message.playerID) != null) {
                PacketSendGangMembers.sendMembers(null, PlayerHelper.getPlayer(message.playerID));
            }

            Notification notification = new Notification(message.playerID, TextFormatting.DARK_GRAY + "You were kicked from your gang.", NotificationType.EDGED, 5, 0xFFFFFF);
            if (PlayerHelper.getPlayer(message.playerID) != null)
                notification.sendTo(PlayerHelper.getPlayer(message.playerID), true, true, false);
            else {
                try {
                    notification.save();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }

}
