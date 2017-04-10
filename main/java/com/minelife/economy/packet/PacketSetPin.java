package com.minelife.economy.packet;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSetPin implements IMessage {

    private String pin;

    public PacketSetPin() {
    }

    public PacketSetPin(String pin) {
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

    public static class Handler implements IMessageHandler<PacketSetPin, IMessage> {

        @Override
        public IMessage onMessage(PacketSetPin message, MessageContext ctx) {
            try {
                ModEconomy.setPin(ctx.getServerHandler().playerEntity.getUniqueID(), message.pin);
                Minelife.NETWORK.sendTo(new PacketUnlockATM(), ctx.getServerHandler().playerEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
