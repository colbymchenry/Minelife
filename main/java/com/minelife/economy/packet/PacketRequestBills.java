package com.minelife.economy.packet;

import com.minelife.Minelife;
import com.minelife.economy.Billing;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class PacketRequestBills implements IMessage {

    public PacketRequestBills() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketRequestBills, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketRequestBills message, MessageContext ctx) {
            Minelife.NETWORK.sendTo(new PacketResponseBills(Billing.getBillsForPlayer(ctx.getServerHandler().playerEntity.getUniqueID())), ctx.getServerHandler().playerEntity);
            return null;
        }
    }
}
