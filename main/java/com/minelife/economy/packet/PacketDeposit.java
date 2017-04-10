package com.minelife.economy.packet;

import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.PacketPlaySound;
import com.minelife.economy.ModEconomy;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketDeposit implements IMessage {

    private long amount;

    public PacketDeposit() {
    }

    public PacketDeposit(long amount) {
        this.amount = amount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.amount = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.amount);
    }

    public static class Handler implements IMessageHandler<PacketDeposit, IMessage> {

        @Override
        public IMessage onMessage(PacketDeposit message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            try {
                if (ModEconomy.getBalance(player.getUniqueID(), true) < message.amount) throw new CustomMessageException("Insufficient funds.");

                ModEconomy.withdraw(player.getUniqueID(), message.amount, true);
                ModEconomy.deposit(player.getUniqueID(), message.amount, false);

                Minelife.NETWORK.sendTo(new PacketUpdateATMGui("Success."), player);
            } catch (CustomMessageException e) {
                Minelife.NETWORK.sendTo(new PacketUpdateATMGui(e.getMessage()), player);
                Minelife.NETWORK.sendTo(new PacketPlaySound("gui.atm.error", 0.5F, 1.0F), player);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
