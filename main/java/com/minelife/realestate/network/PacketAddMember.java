package com.minelife.realestate.network;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.realestate.Permission;
import com.minelife.util.client.PacketPopupMessage;
import com.minelife.util.server.Callback;
import com.minelife.util.server.MLPacket;
import com.minelife.util.server.NameFetcher;
import com.minelife.util.server.UUIDFetcher;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PacketAddMember implements IMessage {

    private String playerName;
    private int estateID;

    public PacketAddMember() {
    }

    public PacketAddMember(String playerName, int estateID) {
        this.playerName = playerName;
        this.estateID = estateID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerName = ByteBufUtils.readUTF8String(buf);
        estateID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerName);
        buf.writeInt(estateID);
    }

    public static class Handler extends MLPacket {

        @Override
        public synchronized void execute(IMessage message, MessageContext ctx) {
            PacketAddMember packet = (PacketAddMember) message;
            EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
            Estate estate = EstateHandler.getEstate(packet.estateID);

            if (estate == null) {
                sender.closeScreen();
                sender.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Something went wrong. That estate could not be found."));
                return;
            }

            if (!estate.getPlayerPermissions(sender.getUniqueID()).contains(Permission.ADD_MEMBER)) {
                sender.closeScreen();
                sender.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have permission to add members."));
                return;
            }

            sender.addChatComponentMessage(new ChatComponentText("Fetching player..."));

            UUID uuid = UUIDFetcher.get(packet.playerName);

            if(uuid == null) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Player not found.", 0xC6C6C6), sender);
                return;
            }

            Map<UUID, Set<Permission>> members = estate.getMembers();

            members.forEach((id, perms) -> System.out.println(id.toString()));
            System.out.println(uuid.toString());

            if(members.containsKey(uuid)) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Player is already a member.", 0xC6C6C6), sender);
                return;
            }

            String name = NameFetcher.get(uuid);
            members.put(uuid, Sets.newTreeSet());
            estate.setMembers(members);
            sender.closeScreen();
            sender.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + name +" added to the members list!"));
        }

    }

}
