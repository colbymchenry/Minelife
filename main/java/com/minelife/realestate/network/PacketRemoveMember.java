package com.minelife.realestate.network;

import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.server.CommandEstate;
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
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class PacketRemoveMember implements IMessage {

    private UUID memberID, estateID;

    public PacketRemoveMember() {
    }

    public PacketRemoveMember(UUID memberID, UUID estateID) {
        this.memberID = memberID;
        this.estateID = estateID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        memberID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        estateID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, memberID.toString());
        ByteBufUtils.writeUTF8String(buf, estateID.toString());
    }

    public static class Handler implements IMessageHandler<PacketRemoveMember, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketRemoveMember message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Estate estate = ModRealEstate.getEstate(message.estateID);

            if(estate == null) {
                player.closeScreen();
                CommandEstate.sendMessage(player, TextFormatting.RED + "Estate not found.");
                return null;
            }

            if(!Objects.equals(estate.getOwnerID(), player.getUniqueID()) && !Objects.equals(estate.getRenterID(), player.getUniqueID())) {
                player.closeScreen();
                CommandEstate.sendMessage(player, TextFormatting.RED + "You are not authorized to modify this estate.");
                return null;
            }

            Set<UUID> members = estate.getMemberIDs();
            members.remove(message.memberID);
            estate.setMemberIDs(members);
            try {
                estate.save();
                Minelife.getNetwork().sendTo(new PacketUpdatedMember(message.memberID, false, estate.getMemberPermissions(message.memberID)), player);
            } catch (SQLException e) {
                e.printStackTrace();
                PacketPopup.sendPopup(TextFormatting.DARK_RED + "An error occurred writing changes. Notify an admin.", player);
            }
            return null;
        }
    }

}
