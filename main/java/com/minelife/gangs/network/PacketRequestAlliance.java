package com.minelife.gangs.network;

import com.google.common.collect.Maps;
import com.minelife.gangs.Gang;
import com.minelife.gangs.GangPermission;
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

import java.util.Map;

public class PacketRequestAlliance implements IMessage {

    public static Map<Gang, Gang> ALLY_REQUESTS = Maps.newHashMap();

    private String gang;

    public PacketRequestAlliance() {
    }

    public PacketRequestAlliance(String gang) {
        this.gang = gang;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gang = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, gang);
    }

    public static class Handler implements IMessageHandler<PacketRequestAlliance, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketRequestAlliance message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Gang playerGang = Gang.getGangForPlayer(player.getUniqueID());

            if(playerGang == null) {
                PacketPopup.sendPopup("You do not belong to a gang.", player);
                return null;
            }

            if(!playerGang.hasPermission(player.getUniqueID(), GangPermission.MANAGE_ALLIANCES)) {
                PacketPopup.sendPopup("You do not have permission to manage alliances.", player);
                return null;
            }

            Gang toAlly = Gang.getGang(message.gang);
            if(toAlly == null) {
                PacketPopup.sendPopup("Gang does not exist.", player);
                return null;
            }

            if(playerGang.getAlliances().contains(toAlly)) {
                PacketPopup.sendPopup("Gang is already an ally.", player);
                return null;
            }

            if(PlayerHelper.getPlayer(toAlly.getOwner()) == null) {
                PacketPopup.sendPopup("Owner of gang must be online.", player);
                return null;
            }

            Notification requestNotification = new Notification(toAlly.getOwner(), TextFormatting.RED + playerGang.getName() + TextFormatting.DARK_GRAY + " gang has requested to become allies with you. " + TextFormatting.RED + "\n/g ally accept" + TextFormatting.DARK_GRAY  + TextFormatting.RED + "\n/g ally deny", NotificationType.EDGED, 8, 0xFFFFFF);
            requestNotification.sendTo(PlayerHelper.getPlayer(toAlly.getOwner()), true, true, false);

            ALLY_REQUESTS.put(toAlly, playerGang);

            return null;
        }
    }

}
