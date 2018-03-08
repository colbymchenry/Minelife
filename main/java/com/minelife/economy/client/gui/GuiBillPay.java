package com.minelife.economy.client.gui;

import com.minelife.Minelife;
import com.minelife.economy.Billing;
import com.minelife.economy.packet.PacketRequestBills;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiLoadingAnimation;
import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;

public class GuiBillPay extends GuiATM {

    private GuiLoadingAnimation loadingAnimation;
    private Content content;
    private List<Billing.Bill> billList;

    public GuiBillPay() {
        Minelife.NETWORK.sendToServer(new PacketRequestBills());
    }

    public GuiBillPay(List<Billing.Bill> bills) {
        this.billList = bills;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (billList == null) {
            loadingAnimation.drawLoadingAnimation();
            return;
        }

        if(billList.isEmpty()) {
            fontRendererObj.drawString(EnumChatFormatting.BOLD + "You have no bills...",
                    (width - fontRendererObj.getStringWidth(EnumChatFormatting.BOLD + "You have no bills...")) / 2,
                    (height - fontRendererObj.FONT_HEIGHT) / 2, 0);
        } else {
            content.draw(mouseX, mouseY, Mouse.getDWheel());
        }
    }

    @Override
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
        super.keyTyped(p_73869_1_, p_73869_2_);
        if (content != null) {
            content.keyTyped(p_73869_1_, p_73869_2_);

            if (p_73869_2_ == Keyboard.KEY_BACK) {
                Minecraft.getMinecraft().thePlayer.playSound(Minelife.MOD_ID + ":key_stroke", 1.0F, mc.theWorld.rand.nextFloat() * 0.1F + 0.8F);
            } else {
                Minecraft.getMinecraft().thePlayer.playSound(Minelife.MOD_ID + ":key_stroke", 1.0F, mc.theWorld.rand.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton p_146284_1_) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
    }

    @Override
    public void initGui() {
        super.initGui();
        if (billList == null) {
            loadingAnimation = new GuiLoadingAnimation((this.width - 75) / 2, (this.height - 75) / 2, 75, 75);
            return;
        }

        content = new Content(mc, (this.width - 200) / 2, (this.height - 200) / 2, 200, 200);
        this.buttonList.add(new ButtonATM(0, 2, this.height - 30 - 2, 75, 30, "Cancel", 2.0));
    }

    private class Content extends GuiScrollableContent {

        private Content(Minecraft mc, int xPosition, int yPosition, int width, int height) {
            super(mc, xPosition, yPosition, width, height);
        }

        @Override
        public int getObjectHeight(int index) {
            return 40 + (fontRendererObj.listFormattedStringToWidth(billList.get(index).getMemo(), this.width).size() * fontRendererObj.FONT_HEIGHT);
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            Billing.Bill bill = billList.get(index);
            fontRendererObj.drawSplitString(bill.getMemo(), 2, 2, 0xFFFFFF, this.width);
            fontRendererObj.drawString("Date Due: " + bill.getDueDateAsString(), 2, 12, 0xFFFFFF);
            fontRendererObj.drawString("Amount Due: $" + NumberConversions.formatter.format(bill.getAmountDue()), 2, 24, 0xFFFFFF);
            fontRendererObj.drawString("Auto Pay: " + (bill.isAutoPay() ? EnumChatFormatting.GREEN + "On" : EnumChatFormatting.RED + "Off"), 2, 36, 0xFFFFFF);
            fontRendererObj.drawString("Pay", width - 25, 36, 0xFFFFFF);
        }

        @Override
        public void drawSelectionBox(int index, int width, int height) {

        }

        @Override
        public int getSize() {
            return billList.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
            Billing.Bill bill = billList.get(index);
            if (mouseX >= 2 && mouseX <= 2 + fontRendererObj.getStringWidth("Auto Pay: " + (bill.isAutoPay() ? "On" : "Off")) && mouseY >= 36 && mouseY <= 36 + fontRendererObj.FONT_HEIGHT) {
                bill.autoPay = !bill.isAutoPay();
                Billing.sendModifyPacketToServer(bill.getUniqueID(), bill.autoPay);
            }

            if (mouseX >= width - 25 && mouseX <= (width - 25) + fontRendererObj.getStringWidth("Pay") && mouseY >= 36 && mouseY <= 36 + fontRendererObj.FONT_HEIGHT) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiPayPopup(bill));
            }
        }
    }

    private class GuiPayPopup extends GuiATM {

        private String amount = "0";
        private Billing.Bill bill;

        public GuiPayPopup(Billing.Bill bill) {
            this.bill = bill;
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            super.drawScreen(mouseX, mouseY, partialTicks);
            drawLabel("Amount Due: $" + NumberConversions.formatter.format(bill.getAmountDue()), (this.width / 2), (this.height / 2), 2, 0xFF03438D);
            drawLabel("$" + NumberConversions.formatter.format(Double.parseDouble(amount)), (this.width / 2), (this.height / 2) + 30, 2, 0xFF03438D);
        }

        @Override
        public void initGui() {
            super.initGui();
            this.buttonList.clear();
            this.buttonList.add(new ButtonATM(0, (this.width - 50) / 2, this.height - 30, 50, 20, "Pay", 2));
            this.buttonList.add(new ButtonATM(1, 2, this.height - 30 - 2, 75, 30, "Cancel", 2.0));
        }

        @Override
        protected void keyTyped(char c, int i) {
            if (i == Keyboard.KEY_ESCAPE) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiBillPay());
                return;
            }

            super.keyTyped(c, i);

            if (i == Keyboard.KEY_BACK) {
                if (amount.length() == 1)
                    amount = "0";
                else
                    amount = amount.substring(0, amount.length() - 1);
                return;
            }

            if (NumberConversions.isInt("" + c)) {
                amount += c;
            }
        }

        @Override
        protected void actionPerformed(GuiButton btn) {
            super.actionPerformed(btn);

            if (btn.id == 1) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiBillPay());
                return;
            }

            if (Integer.parseInt(amount) > 0) {
                Billing.sendPayPacketToServer(bill.getUniqueID(), Integer.parseInt(amount));
            }
        }
    }




}
