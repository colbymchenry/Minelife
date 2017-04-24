package com.minelife.economy.packet;

import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.PacketPlaySound;
import com.minelife.util.server.UUIDFetcher;
import com.minelife.economy.ModEconomy;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.UUID;

public class PacketTransferMoney implements IMessage {

    private String player;
    private long amount;

    public PacketTransferMoney() {
    }

    public PacketTransferMoney(String player, long amount) {
        this.player = player;
        this.amount = amount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.player = ByteBufUtils.readUTF8String(buf);
        this.amount = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.player);
        buf.writeLong(this.amount);
    }

    public static class Handler implements IMessageHandler<PacketTransferMoney, IMessage> {

        @Override
        public IMessage onMessage(PacketTransferMoney message, MessageContext ctx) {
            EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
            UUID playerUUID = UUIDFetcher.get(message.player);

            try {
                if (playerUUID == null) throw new CustomMessageException("Player not found.");
                if(!ModEconomy.playerExists(playerUUID)) throw new CustomMessageException("Player not found.");

                boolean insufficientFunds = ModEconomy.getBalance(sender.getUniqueID(), false) < message.amount;

                if (insufficientFunds) throw new CustomMessageException("Insufficient funds.");

                ModEconomy.withdraw(sender.getUniqueID(), message.amount,false);
                ModEconomy.deposit(playerUUID, message.amount, false);

                Minelife.NETWORK.sendTo(new PacketUpdateATMGui("Success."), sender);
            } catch (CustomMessageException e) {
                Minelife.NETWORK.sendTo(new PacketUpdateATMGui(e.getMessage()), sender);
                Minelife.NETWORK.sendTo(new PacketPlaySound("gui.atm.error", 0.5F, 1.0F), sender);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
