package com.minelife.economy.network;

import com.google.common.collect.Sets;
import com.minelife.economy.Bill;
import com.minelife.economy.client.gui.atm.GuiATMBills;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

public class PacketSendBills implements IMessage {

    private long balance;
    private Set<Bill> bills;

    public PacketSendBills(long balance, Set<Bill> bills) {
        this.balance = balance;
        this.bills = bills;
    }

    public PacketSendBills() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        balance = buf.readLong();
        bills = Sets.newTreeSet();
        int size = buf.readInt();

        for (int i = 0; i < size; i++) bills.add(Bill.fromBytes(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(balance);
        buf.writeInt(bills.size());
        bills.forEach(bill -> bill.toBytes(buf));
    }

    public static class Handler implements IMessageHandler<PacketSendBills, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSendBills message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiATMBills(message.balance, message.bills)));
            return null;
        }
    }
}
