package com.minelife.realestate.network;

import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

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

            Minelife.NETWORK.sendTo(new PacketSendMembers(estate.getMembers(), estate.getPlayerPermissions(ctx.getServerHandler().playerEntity), estate.getID()), ctx.getServerHandler().playerEntity);
            return null;
        }
    }
}
