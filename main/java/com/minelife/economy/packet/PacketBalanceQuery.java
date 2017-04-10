package com.minelife.economy.packet;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketBalanceQuery implements IMessage {

    public PacketBalanceQuery() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketBalanceQuery, IMessage> {

        @Override
        public IMessage onMessage(PacketBalanceQuery message, MessageContext ctx) {
            try {
                long balanceBank = ModEconomy.getBalance(ctx.getServerHandler().playerEntity.getUniqueID(), false);
                long balanceWallet = ModEconomy.getBalance(ctx.getServerHandler().playerEntity.getUniqueID(), true);
                Minelife.NETWORK.sendTo(new PacketBalanceResult(balanceBank, balanceWallet), ctx.getServerHandler().playerEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
