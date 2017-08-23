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

    private double balanceBank, balanceWallet;

    public PacketBalanceResult() {
    }

    public PacketBalanceResult(double balanceBank, double balanceWallet) {
        this.balanceBank = balanceBank;
        this.balanceWallet = balanceWallet;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.balanceBank = buf.readDouble();
        this.balanceWallet = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.balanceBank);
        buf.writeDouble(this.balanceWallet);
    }

    public static class Handler implements IMessageHandler<PacketBalanceResult, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketBalanceResult message, MessageContext ctx) {
            ModEconomy.BALANCE_WALLET_CLIENT = message.balanceWallet;
            ModEconomy.BALANCE_BANK_CLIENT = message.balanceBank;
            return null;
        }

    }

}
