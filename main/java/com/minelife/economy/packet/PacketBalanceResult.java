package com.minelife.economy.packet;

import com.minelife.economy.ModEconomy;
import com.minelife.economy.client.OnScreenRenderer;
import com.minelife.economy.client.gui.GuiBalance;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class PacketBalanceResult implements IMessage {

    private int balanceBank;

    public PacketBalanceResult() {
    }

    public PacketBalanceResult(int balanceBank) {
        this.balanceBank = balanceBank;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.balanceBank = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.balanceBank);
    }

    public static class Handler implements IMessageHandler<PacketBalanceResult, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketBalanceResult message, MessageContext ctx) {
            ModEconomy.BALANCE_BANK_CLIENT = message.balanceBank;
            return null;
        }

    }

}
