package com.minelife.economy.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.economy.Billing;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiLoadingAnimation;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.GuiTickBox;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

import java.util.List;
import java.util.Map;

public class GuiBillPay extends GuiATM {

    private GuiLoadingAnimation loadingAnimation;
    private Content content;
    private List<Billing.Bill> billList;

    GuiBillPay()
    {
        Minelife.NETWORK.sendToServer(new PacketRequestBills());
    }

    private GuiBillPay(List<Billing.Bill> bills)
    {
        this.billList = bills;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (billList == null) {
            loadingAnimation.drawLoadingAnimation();
            return;
        }

        content.draw(mouseX, mouseY, Mouse.getDWheel());
    }

    @Override
    protected void keyTyped(char p_73869_1_, int p_73869_2_)
    {
        super.keyTyped(p_73869_1_, p_73869_2_);
        if(content != null) content.keyTyped(p_73869_1_, p_73869_2_);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        if (billList == null) {
            loadingAnimation = new GuiLoadingAnimation((this.width - 75) / 2, (this.height - 75) / 2, 75, 75);
            return;
        }

        content = new Content((this.width - 200) / 2, (this.height - 200) / 2, 200, 200);
    }

    private class Content extends GuiScrollableContent {

        private Map<Billing.Bill, GuiTickBox> tickBoxMap = Maps.newHashMap();

        private Content(int xPosition, int yPosition, int width, int height)
        {
            super(xPosition, yPosition, width, height);
            billList.forEach(bill -> tickBoxMap.put(bill, new GuiTickBox(mc, 30, 36, bill.isAutoPay())));
        }

        @Override
        public int getObjectHeight(int index)
        {
            return 40 + (fontRendererObj.listFormattedStringToWidth(billList.get(index).getMemo(), this.width).size() * fontRendererObj.FONT_HEIGHT);
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            Billing.Bill bill = billList.get(index);
            fontRendererObj.drawSplitString(bill.getMemo(), 2, 2, 0xFFFFFF, this.width);
            fontRendererObj.drawString("Date Due: " + bill.getDueDateAsString(), 2, 12, 0xFFFFFF);
            fontRendererObj.drawString("Amount Due: $" + NumberConversions.formatter.format(bill.getAmountDue()), 2, 24, 0xFFFFFF);
            fontRendererObj.drawString("Auto Pay: ", 2, 36, 0xFFFFFF);
            tickBoxMap.values().forEach(GuiTickBox::drawTickBox);
        }

        @Override
        public int getSize()
        {
            return billList.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            // TODO: Pay bill
        }
    }

    public static class PacketRequestBills implements IMessage {

        public PacketRequestBills()
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {

        }

        @Override
        public void toBytes(ByteBuf buf)
        {

        }

        public static class Handler implements IMessageHandler<PacketRequestBills, IMessage> {

            @SideOnly(Side.SERVER)
            public IMessage onMessage(PacketRequestBills message, MessageContext ctx)
            {
                Minelife.NETWORK.sendTo(new PacketResponseBills(Billing.getBillsForPlayer(ctx.getServerHandler().playerEntity.getUniqueID())), ctx.getServerHandler().playerEntity);
                return null;
            }
        }
    }

    public static class PacketResponseBills implements IMessage {

        private List<Billing.Bill> billList;

        public PacketResponseBills()
        {
        }

        public PacketResponseBills(List<Billing.Bill> bills)
        {
            this.billList = bills;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            billList = Lists.newArrayList();
            int billCount = buf.readInt();
            for (int i = 0; i < billCount; i++) billList.add(Billing.Bill.fromBytes(buf));
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(billList.size());
            billList.forEach(bill -> bill.toBytes(buf));
        }

        public static class Handler implements IMessageHandler<PacketResponseBills, IMessage> {

            @SideOnly(Side.CLIENT)
            public IMessage onMessage(PacketResponseBills message, MessageContext ctx)
            {
                Minecraft.getMinecraft().displayGuiScreen(new GuiBillPay(message.billList));
                return null;
            }
        }
    }


}
