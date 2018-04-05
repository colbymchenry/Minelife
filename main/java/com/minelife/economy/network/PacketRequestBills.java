package com.minelife.economy.network;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.realestate.BillHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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

        @Override
        public IMessage onMessage(PacketRequestBills message, MessageContext ctx) {
            Minelife.getNetwork().sendTo(new PacketSendBills(ModEconomy.getBalanceATM(ctx.getServerHandler().player.getUniqueID()),
                    BillHandler.getRentBills(ctx.getServerHandler().player.getUniqueID())), ctx.getServerHandler().player);
            return null;
        }
    }

}
