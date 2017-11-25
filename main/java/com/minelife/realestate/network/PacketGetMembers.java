package com.minelife.realestate.network;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.realestate.Permission;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

import java.util.Set;

public class PacketGetMembers implements IMessage {

    // TODO
    private int estateID;

    public PacketGetMembers() {
    }

    public PacketGetMembers(int estateID) {
        this.estateID = estateID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        estateID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(estateID);
    }

    public static class Handler implements IMessageHandler<PacketGetMembers, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketGetMembers message, MessageContext ctx) {
            Estate estate = EstateHandler.getEstate(message.estateID);

            if(estate == null) return null;

            Set<Permission> perms = estate.getPlayerPermissions(ctx.getServerHandler().playerEntity);
            perms.removeAll(Permission.getEstatePermissions());

            Minelife.NETWORK.sendTo(new PacketSendMembers(estate.getMembers(), perms, estate.getID()), ctx.getServerHandler().playerEntity);
            return null;
        }
    }
}
