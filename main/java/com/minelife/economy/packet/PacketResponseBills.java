package com.minelife.economy.packet;

import com.google.common.collect.Lists;
import com.minelife.economy.Billing;
import com.minelife.economy.client.gui.GuiBillPay;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.util.List;

public class PacketResponseBills implements IMessage {

    private List<Billing.Bill> billList;

    public PacketResponseBills() {
    }

    public PacketResponseBills(List<Billing.Bill> bills) {
        this.billList = bills;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        billList = Lists.newArrayList();
        int billCount = buf.readInt();
        for (int i = 0; i < billCount; i++) billList.add(Billing.Bill.fromBytes(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(billList.size());
        for (Billing.Bill bill : billList) bill.toBytes(buf);
    }

    public static class Handler implements IMessageHandler<PacketResponseBills, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketResponseBills message, MessageContext ctx) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiBillPay(message.billList));
            return null;
        }
    }
}