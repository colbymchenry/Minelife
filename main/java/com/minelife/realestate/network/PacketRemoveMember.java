package com.minelife.realestate.network;

import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.realestate.Permission;
import com.minelife.util.client.PacketPopupMessage;
import com.minelife.util.server.MLPacket;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PacketRemoveMember implements IMessage {

    private UUID memberUUID;
    private int estateID;

    public PacketRemoveMember() {
    }

    public PacketRemoveMember(UUID memberUUID, int estateID) {
        this.memberUUID = memberUUID;
        this.estateID = estateID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        memberUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        estateID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, memberUUID.toString());
        buf.writeInt(estateID);
    }

    public static class Handler implements IMessageHandler<PacketRemoveMember, IMessage> {

        @Override
        public IMessage onMessage(PacketRemoveMember message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            Estate estate = EstateHandler.getEstate(message.estateID);

            if(estate == null) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Could not find estate.", 0xC6C6C6), player);
                return null;
            }

            if(!estate.getPlayerPermissions(player.getUniqueID()).contains(Permission.REMOVE_MEMBER)) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("You do not have permission to remove members from this estate.", 0xC6C6C6), player);
                return null;
            }

            Map<UUID, Set<Permission>> members = estate.getMembers();
            members.remove(message.memberUUID);
            estate.setMembers(members);
            player.closeScreen();
            player.addChatComponentMessage(new ChatComponentText("Member removed!"));
            return null;
        }
    }

}
