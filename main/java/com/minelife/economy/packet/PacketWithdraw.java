package com.minelife.economy.packet;

import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.economy.MoneyHandler;
import com.minelife.util.PacketPlaySound;
import com.minelife.economy.ModEconomy;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketWithdraw implements IMessage {

    private int amount;

    public PacketWithdraw() {
    }

    public PacketWithdraw(int amount) {
        this.amount = amount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.amount);
    }

    public static class Handler implements IMessageHandler<PacketWithdraw, IMessage> {

        @Override
        public IMessage onMessage(PacketWithdraw message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            try {
                if (MoneyHandler.getBalanceATM(player.getUniqueID()) < message.amount) throw new CustomMessageException("Insufficient funds.");

                // TODO: Test this to make sure you can't duplicate money
                MoneyHandler.withdrawATM(player.getUniqueID(), message.amount);
                MoneyHandler.addMoneyInventory(player, message.amount);

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
