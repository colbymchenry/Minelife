package com.minelife.economy.packet;

import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.PacketPlaySound;
import com.minelife.economy.ModEconomy;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketVerifyPin implements IMessage {

    private String pin;

    public PacketVerifyPin() {
    }

    public PacketVerifyPin(String pin) {
        this.pin = pin;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pin = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.pin);
    }

    public static class Handler implements IMessageHandler<PacketVerifyPin, IMessage> {

        @Override
        public IMessage onMessage(PacketVerifyPin message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            try {
                if (!ModEconomy.getPin(player.getUniqueID()).equalsIgnoreCase(message.pin)) throw new CustomMessageException("Incorrect pin.");

                Minelife.NETWORK.sendTo(new PacketUnlockATM(), player);
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
