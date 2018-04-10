package com.minelife.realestate.network;

import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.PlayerPermission;
import com.minelife.util.client.PacketPopup;
import com.minelife.util.server.NameUUIDCallback;
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

import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public class PacketAddMember implements IMessage {

    private String playerName;
    private UUID estateUniqueID;

    public PacketAddMember() {
    }

    public PacketAddMember(UUID estateUniqueID, String playerName) {
        this.estateUniqueID = estateUniqueID;
        this.playerName = playerName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        estateUniqueID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        playerName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, estateUniqueID.toString());
        ByteBufUtils.writeUTF8String(buf, playerName);
    }

    public static class Handler implements IMessageHandler<PacketAddMember, IMessage>, NameUUIDCallback {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketAddMember message, MessageContext ctx) {
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

            UUIDFetcher.get(message.playerName, this, estate, player);
            return null;
        }

        @Override
        public void callback(UUID id, String name, Object... objects) {
            Estate estate = (Estate) objects[0];
            EntityPlayerMP player = (EntityPlayerMP) objects[1];

            if(id == null) {
                PacketPopup.sendPopup("Player not found by that name.", player);
                return;
            }

            Set<UUID> memberIDs = estate.getMemberIDs();
            memberIDs.add(id);
            estate.setMemberIDs(memberIDs);
            try {
                estate.save();

                Minelife.getNetwork().sendTo(new PacketUpdatedMember(id, true, estate.getMemberPermissions(id)), player);
            } catch (SQLException e) {
                e.printStackTrace();
                PacketPopup.sendPopup(TextFormatting.DARK_RED + "Error writing changes. Please notify an admin.", player);
            }
        }
    }

}
