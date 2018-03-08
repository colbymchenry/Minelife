package com.minelife.gangs.network;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.gangs.Gang;
import com.minelife.gangs.ModGangs;
import com.minelife.util.PlayerHelper;
import com.minelife.util.client.PacketPopupMessage;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Map;
import java.util.UUID;

public class PacketInviteToGang implements IMessage {

    public static final Map<UUID, Gang> gangInvites = Maps.newHashMap();

    private String playerName;

    public PacketInviteToGang() {
    }

    public PacketInviteToGang(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerName);
    }

    public static class Handler implements IMessageHandler<PacketInviteToGang, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketInviteToGang message, MessageContext ctx) {
            EntityPlayerMP playerSender = ctx.getServerHandler().playerEntity;
            EntityPlayerMP playerReceiver = PlayerHelper.getPlayer(message.playerName);
            Gang senderGang = ModGangs.getPlayerGang(playerSender.getUniqueID());

            if(senderGang == null) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("You do not belong to a gang."), playerSender);
                return null;
            }

            if (!senderGang.getLeader().equals(playerSender.getUniqueID()) && !senderGang.getOfficers().contains(playerSender.getUniqueID())) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("You do not have permission to add members to the gang."), playerSender);
                return null;
            }

            if(playerReceiver == null) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Player not found."), playerSender);
                return null;
            }

            Gang receiverGang = ModGangs.getPlayerGang(playerReceiver.getUniqueID());

            if(receiverGang != null) {
                playerSender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Player is already in a gang."));
                return null;
            }

            if(gangInvites.containsKey(playerReceiver.getUniqueID()) && gangInvites.get(playerReceiver.getUniqueID()).equals(senderGang)) return null;

            gangInvites.put(playerReceiver.getUniqueID(), senderGang);
            playerReceiver.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + playerSender.getDisplayName() + EnumChatFormatting.GOLD + " has invited you to join their gang. Type " + EnumChatFormatting.RED + "/g accept " + EnumChatFormatting.GOLD + "or " + EnumChatFormatting.RED + "/g deny"));
            return null;
        }
    }

}
