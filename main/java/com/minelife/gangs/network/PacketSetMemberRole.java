package com.minelife.gangs.network;

import com.google.common.collect.Maps;
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
import org.apache.commons.lang3.text.WordUtils;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class PacketSetMemberRole implements IMessage {

    private UUID memberID;
    private GangRole role;

    public PacketSetMemberRole() {
    }

    public PacketSetMemberRole(UUID memberID, GangRole role) {
        this.memberID = memberID;
        this.role = role;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        memberID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        role = GangRole.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, memberID.toString());
        buf.writeInt(role.ordinal());
    }

    public static class Handler implements IMessageHandler<PacketSetMemberRole, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketSetMemberRole message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Gang playerGang = Gang.getGangForPlayer(player.getUniqueID());

            if (playerGang == null) {
                PacketPopup.sendPopup("You do not belong to a gang.", player);
                return null;
            }

            if (!playerGang.hasPermission(player.getUniqueID(), GangPermission.PROMOTE_AND_DEMOTE)) {
                PacketPopup.sendPopup("You do not have permission to promote and demote players.", player);
                return null;
            }

            if (playerGang.getMembers().containsKey(message.memberID) && playerGang.getMembers().get(message.memberID) == GangRole.OLDER &&
                    !playerGang.getOwner().equals(player.getUniqueID())) {
                PacketPopup.sendPopup("Only the owner can demote an older.", player);
                return null;
            }

            Map<UUID, GangRole> members = playerGang.getMembers();
            members.put(message.memberID, message.role);
            playerGang.setMembers(members);
            playerGang.writeToDatabase();

            Minelife.getNetwork().sendTo(new PacketOpenGangGui(playerGang), player);

            Notification notification = new Notification(message.memberID, TextFormatting.DARK_GRAY + "You rank has been changed to " + TextFormatting.RED + WordUtils.capitalizeFully(message.role.name()), NotificationType.EDGED, 5, 0xFFFFFF);
            if (PlayerHelper.getPlayer(message.memberID) != null)
                notification.sendTo(PlayerHelper.getPlayer(message.memberID), true, true, false);
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
