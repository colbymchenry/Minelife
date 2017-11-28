package com.minelife.realestate.network;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.realestate.Permission;
import com.minelife.util.client.PacketPopupMessage;
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

public class PacketModifyMember implements IMessage {

    private int estateID;
    private UUID memberUUID;
    private Set<Permission> permissionSet;

    public PacketModifyMember() {
    }

    public PacketModifyMember(int estateID, UUID memberUUID, Set<Permission> permissionSet) {
        this.estateID = estateID;
        this.memberUUID = memberUUID;
        this.permissionSet = permissionSet;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        estateID = buf.readInt();
        memberUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        permissionSet = Sets.newTreeSet();
        int permsSize = buf.readInt();
        for (int i = 0; i < permsSize; i++) permissionSet.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(estateID);
        ByteBufUtils.writeUTF8String(buf, memberUUID.toString());
        buf.writeInt(permissionSet.size());
        permissionSet.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
    }

    public static class Handler implements IMessageHandler<PacketModifyMember, IMessage> {

        @Override
        public IMessage onMessage(PacketModifyMember message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            Estate estate = EstateHandler.getEstate(message.estateID);

            if(estate == null) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Could not find estate."), player);
                return null;
            }

            if(!estate.getPlayerPermissions(player.getUniqueID()).contains(Permission.ADD_MEMBER) ||
                    !estate.getPlayerPermissions(player.getUniqueID()).contains(Permission.REMOVE_MEMBER)) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("You are not authorized to modify members of this estate."), player);
                return null;
            }

            Map<UUID, Set<Permission>> members = estate.getMembers();
            if(!members.containsKey(message.memberUUID)) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Player is not a member of the estate."), player);
                return null;
            }

            Set<Permission> toRemove = Sets.newTreeSet();

            // remove the permissions the player does not have.
            message.permissionSet.forEach(p -> {
                if(!estate.getPlayerPermissions(player.getUniqueID()).contains(p)) toRemove.add(p);
            });

            message.permissionSet.removeAll(toRemove);
            members.put(message.memberUUID, message.permissionSet);
            estate.setMembers(members);
            return null;
        }
    }

}
