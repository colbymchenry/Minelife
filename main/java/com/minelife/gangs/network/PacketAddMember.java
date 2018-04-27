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
import com.minelife.util.server.UUIDFetcher;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PacketAddMember implements IMessage {

    public static Map<UUID, Gang> GANG_INVITES = Maps.newHashMap();

    private UUID gangID;
    private String name;

    public PacketAddMember() {
    }

    public PacketAddMember(UUID gangID, String name) {
        this.gangID = gangID;
        this.name = name;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gangID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        name = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, gangID.toString());
        ByteBufUtils.writeUTF8String(buf, name);
    }

    public static class Handler implements IMessageHandler<PacketAddMember, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketAddMember message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Gang gang = Gang.getGang(message.gangID);

            if (gang == null) {
                PacketPopup.sendPopup("Gang not found", player);
                return null;
            }

            if (gang.hasPermission(player.getUniqueID(), GangPermission.INVITE)) {
                UUID playerID = UUIDFetcher.get(message.name);

                if (playerID == null) {
                    PacketPopup.sendPopup("Player not found", player);
                    return null;
                }


                if (Gang.getGangForPlayer(playerID) != null) {
                    PacketPopup.sendPopup("Player already belongs to a gang", player);
                    return null;
                }

                if (PlayerHelper.getPlayer(playerID) == null) {
                    PacketPopup.sendPopup("Player not online.", player);
                    return null;
                }

                GANG_INVITES.put(playerID, gang);

                Notification notification = new Notification(playerID, TextFormatting.DARK_RED + gang.getName() + TextFormatting.DARK_GRAY + " invited you to join them.\n/g accept\n/g deny", NotificationType.EDGED, 7, 0xFFFFFF);
                notification.sendTo(PlayerHelper.getPlayer(playerID), true, true, false);
            } else {
                PacketPopup.sendPopup("You do not have permission to invite players.", player);
            }
            return null;
        }
    }

}
