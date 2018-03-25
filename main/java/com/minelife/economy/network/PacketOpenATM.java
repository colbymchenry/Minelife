package com.minelife.economy.network;

import com.minelife.economy.client.gui.atm.GuiATMMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketOpenATM implements IMessage {

    private long balance;

    public PacketOpenATM() {
    }

    public PacketOpenATM(long balance) {
        this.balance = balance;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.balance = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.balance);
    }

    public static class Handler implements IMessageHandler<PacketOpenATM, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenATM message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiATMMenu(message.balance)));
            return null;
        }
    }

}
