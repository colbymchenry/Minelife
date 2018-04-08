package com.minelife.realestate.network;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.PlayerPermission;
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
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class PacketModifyMember implements IMessage {

    private UUID memberUniqueID, estateUniqueID;
    private Set<PlayerPermission> permissions;

    public PacketModifyMember() {
    }

    public PacketModifyMember(UUID memberUniqueID, UUID estateUniqueID, Set<PlayerPermission> permissions) {
        this.memberUniqueID = memberUniqueID;
        this.estateUniqueID = estateUniqueID;
        this.permissions = permissions;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        memberUniqueID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        estateUniqueID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        int permsSize = buf.readInt();
        permissions = Sets.newTreeSet();
        for (int i = 0; i < permsSize; i++) permissions.add(PlayerPermission.values()[buf.readInt()]);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, memberUniqueID.toString());
        ByteBufUtils.writeUTF8String(buf, estateUniqueID.toString());
        buf.writeInt(permissions.size());
        permissions.forEach(playerPermission -> buf.writeInt(playerPermission.ordinal()));
    }

    public static class Handler implements IMessageHandler<PacketModifyMember, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketModifyMember message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Estate estate = ModRealEstate.getEstate(message.estateUniqueID);

            if(estate == null) {
                PacketPopup.sendPopup("Estate not found.", player);
                return null;
            }

            Set<PlayerPermission> permissions =  estate.getPlayerPermissions(player.getUniqueID());

            if(!permissions.contains(PlayerPermission.MODIFY_MEMBERS)) {
                PacketPopup.sendPopup("You do not have permission to modify the members of this estate.", player);
                return null;
            }

            Set<UUID> memberIDs = estate.getMemberIDs();
            memberIDs.add(message.memberUniqueID);

            Iterator<PlayerPermission> iterator = message.permissions.iterator();
            while (iterator.hasNext()) if(!permissions.contains(iterator.next())) iterator.remove();

            estate.setMemberIDs(memberIDs);
            estate.setMemberPermissions(message.memberUniqueID, message.permissions);
            try {
                estate.save();
            } catch (SQLException e) {
                e.printStackTrace();
                PacketPopup.sendPopup(TextFormatting.DARK_RED + "There was an error writing changes. Notify an admin.", player);
            }
            return null;
        }
    }

}
