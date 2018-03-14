package com.minelife.economy.packet;

import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.economy.MoneyHandler;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.server.NameUUIDCallback;
import com.minelife.util.server.UUIDFetcher;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.UUID;

public class PacketTransferMoney implements IMessage {

    private String player;
    private int amount;

    public PacketTransferMoney() {
    }

    public PacketTransferMoney(String player, int amount) {
        this.player = player;
        this.amount = amount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.player = ByteBufUtils.readUTF8String(buf);
        this.amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.player);
        buf.writeInt(this.amount);
    }

    public static class Handler implements IMessageHandler<PacketTransferMoney, IMessage>, NameUUIDCallback {

        @Override
        public IMessage onMessage(PacketTransferMoney message, MessageContext ctx) {
            UUIDFetcher.get(message.player, this, message, ctx);
            return null;
        }

        @Override
        public void callback(UUID id, String name, Object... objects) {
            PacketTransferMoney message = (PacketTransferMoney) objects[0];
            MessageContext ctx = (MessageContext) objects[1];
            EntityPlayerMP sender = ctx.getServerHandler().playerEntity;

            try {
                if (id == null) throw new CustomMessageException("Player not found.");
                if (!MoneyHandler.hasATM(id)) throw new CustomMessageException("Player not found.");

                boolean insufficientFunds = MoneyHandler.getBalanceATM(sender.getUniqueID()) < message.amount;

                if (insufficientFunds) throw new CustomMessageException("Insufficient funds.");

                MoneyHandler.withdrawATM(sender.getUniqueID(), message.amount);
                MoneyHandler.depositATM(id, message.amount);

                Minelife.NETWORK.sendTo(new PacketUpdateATMGui("Success."), sender);
            } catch (CustomMessageException e) {
                Minelife.NETWORK.sendTo(new PacketUpdateATMGui(e.getMessage()), sender);
                Minelife.NETWORK.sendTo(new PacketPlaySound("gui.atm.error", 0.5F, 1.0F), sender);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
