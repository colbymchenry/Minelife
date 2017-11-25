package com.minelife.realestate.client.gui;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.realestate.Permission;
import com.minelife.realestate.network.PacketPurchaseEstate;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.GuiTickBox;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GuiPurchaseEstate extends GuiScreen {

    private GuiContent content;

    private double purchasePrice, rentPrice;
    private int estateID, rentPeriod;
    private Set<Permission> globalPerms, renterPerms, ownerPerms, allowedToChangePerms, estatePerms;
    private int bgWidth = 200, bgHeight = 200, xPosition, yPosition;

    public GuiPurchaseEstate(int estateID, double purchasePrice, double rentPrice, int rentPeriod, Set<Permission> globalPerms,
                             Set<Permission> renterPerms, Set<Permission> ownerPerms, Set<Permission> allowedToChangePerms,
                             Set<Permission> estatePerms) {
        this.estateID = estateID;
        this.purchasePrice = purchasePrice;
        this.rentPrice = rentPrice;
        this.rentPeriod = rentPeriod;
        this.globalPerms = globalPerms;
        this.renterPerms = renterPerms;
        this.ownerPerms = ownerPerms;
        this.allowedToChangePerms = allowedToChangePerms;
        this.estatePerms = estatePerms;
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        drawDefaultBackground();
        GuiUtil.drawDefaultBackground(xPosition, yPosition, bgWidth, bgHeight);
        content.draw(x, y, Mouse.getDWheel());
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);
        content.keyTyped(keyChar, keyCode);
    }

    @Override
    public void initGui() {
        super.initGui();
        xPosition = (width - bgWidth) / 2;
        yPosition = (height - bgHeight) / 2;
        content = new GuiContent(mc, xPosition, yPosition + 4, bgWidth, bgHeight - 6);
    }

    private class GuiContent extends GuiScrollableContent {

        private int section = (width / 2);
        private int totalHeight;
        private Map<Permission, GuiTickBox> globalTicks, renterTicks, ownerTicks, allowedToChangeTicks, estateTicks;
        private int globalY, renterY, ownerY, allowedToChangeY, estateY;

        private String bold = EnumChatFormatting.BOLD.toString(), red = EnumChatFormatting.RED.toString();
        private String purchasePriceStr, rentPriceStr, rentPeriodStr;
        private int purchasePriceW, rentPriceW, rentPeriodW;

        private GuiButton purchaseBtn, rentBtn;

        public GuiContent(Minecraft mc, int xPosition, int yPosition, int width, int height) {
            super(mc, xPosition, yPosition, width, height);
            bold = EnumChatFormatting.BOLD.toString();
            red = EnumChatFormatting.RED.toString();
            purchasePriceStr = "Purchase: " + (purchasePrice == -1 ? "Not for Sale" : "$" + NumberConversions.formatter.format(purchasePrice));
            rentPriceStr = "Rent: " + (rentPrice == -1 ? "Not for Rent" : "$" + NumberConversions.formatter.format(rentPrice));
            rentPeriodStr = "Rent Period: " + rentPeriod;
            purchasePriceW = fontRendererObj.getStringWidth(purchasePriceStr);
            rentPriceW = fontRendererObj.getStringWidth(rentPriceStr);
            rentPeriodW = fontRendererObj.getStringWidth(rentPeriodStr);

            globalTicks = Maps.newHashMap();
            renterTicks = Maps.newHashMap();
            ownerTicks = Maps.newHashMap();
            allowedToChangeTicks = Maps.newHashMap();
            estateTicks = Maps.newHashMap();

            totalHeight += 100;
            globalY = totalHeight;
            globalPerms.forEach(p -> globalTicks.put(p, new GuiTickBox(mc, section * 1 + ((section - GuiTickBox.WIDTH) / 2), totalHeight += 20, true)));
            totalHeight += 40;
            renterY = totalHeight;
            renterPerms.forEach(p -> renterTicks.put(p, new GuiTickBox(mc, section * 1 + ((section - GuiTickBox.WIDTH) / 2), totalHeight += 20, true)));
            totalHeight += 40;
            ownerY = totalHeight;
            ownerPerms.forEach(p -> ownerTicks.put(p, new GuiTickBox(mc, section * 1 + ((section - GuiTickBox.WIDTH) / 2), totalHeight += 20, true)));
            totalHeight += 40;
            allowedToChangeY = totalHeight;
            totalHeight += 20;
            allowedToChangePerms.forEach(p -> allowedToChangeTicks.put(p, new GuiTickBox(mc, section * 1 + ((section - GuiTickBox.WIDTH) / 2), totalHeight += 20, true)));
            totalHeight += 60;
            estateY = totalHeight;
            estatePerms.forEach(p -> estateTicks.put(p, new GuiTickBox(mc, section * 1 + ((section - GuiTickBox.WIDTH) / 2), totalHeight += 20, true)));

            totalHeight += 40;
            rentBtn = new GuiButton(0, (section - 50) / 2, totalHeight, 50, 20, "Rent");
            purchaseBtn = new GuiButton(1, section + ((section - 50) / 2), totalHeight, 50, 20, "Purchase");
            rentBtn.enabled = rentPrice != -1;
            purchaseBtn.enabled = purchasePrice != -1;
        }

        @Override
        public int getObjectHeight(int index) {
            return totalHeight + 40;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            fontRendererObj.drawString(bold + purchasePriceStr, (width - purchasePriceW) / 2, 20, 0xFFFFFF);
            fontRendererObj.drawString(bold + rentPriceStr, (width - rentPriceW) / 2, 40, 0xFFFFFF);
            fontRendererObj.drawString(bold + rentPeriodStr, (width - rentPeriodW) / 2, 60, 0xFFFFFF);

            fontRendererObj.drawString(bold + "Global Permissions", (width - fontRendererObj.getStringWidth(bold + "Global Permissions")) / 2, globalY, 0xFFFFFF);


            if (globalTicks.isEmpty()) {
                fontRendererObj.drawString(red + bold + "None", (width - fontRendererObj.getStringWidth(bold + red + "None")) / 2, globalY + 20, 0xFFFFFF);
            }

            globalTicks.forEach((p, tB) -> {
                fontRendererObj.drawString(bold + p.name(), 5 + (((width + 40) / 2) - fontRendererObj.getStringWidth(bold + p.name())) / 2, tB.yPosition + 5, 0xFFFFFF);
                tB.drawTickBox();
            });

            fontRendererObj.drawString(bold + "Renter Permissions", (width - fontRendererObj.getStringWidth(bold + "Renter Permissions")) / 2, renterY, 0xFFFFFF);


            if (renterTicks.isEmpty()) {
                fontRendererObj.drawString(red + bold + "None", (width - fontRendererObj.getStringWidth(bold + red + "None")) / 2, renterY + 20, 0xFFFFFF);
            }

            renterTicks.forEach((p, tB) -> {
                fontRendererObj.drawString(bold + p.name(), 5 + (((width + 40) / 2) - fontRendererObj.getStringWidth(bold + p.name())) / 2, tB.yPosition + 5, 0xFFFFFF);
                tB.drawTickBox();
            });

            fontRendererObj.drawString(bold + "Owner Permissions", (width - fontRendererObj.getStringWidth(bold + "Owner Permissions")) / 2, ownerY, 0xFFFFFF);

            if (ownerTicks.isEmpty()) {
                fontRendererObj.drawString(red + bold + "None", (width - fontRendererObj.getStringWidth(bold + red + "None")) / 2, ownerY + 20, 0xFFFFFF);
            }

            ownerTicks.forEach((p, tB) -> {
                fontRendererObj.drawString(bold + p.name(), 5 + (((width + 40) / 2) - fontRendererObj.getStringWidth(bold + p.name())) / 2, tB.yPosition + 5, 0xFFFFFF);
                tB.drawTickBox();
            });

            fontRendererObj.drawString(bold + "Global Permissions", (width - fontRendererObj.getStringWidth(bold + "Global Permissions")) / 2, allowedToChangeY, 0xFFFFFF);
            fontRendererObj.drawString(bold + "Allowed to Change", (width - fontRendererObj.getStringWidth(bold + "Allowed to Change")) / 2, allowedToChangeY + 20, 0xFFFFFF);

            if (allowedToChangeTicks.isEmpty()) {
                fontRendererObj.drawString(red + bold + "None", (width - fontRendererObj.getStringWidth(bold + red + "None")) / 2, allowedToChangeY + 40, 0xFFFFFF);
            }

            allowedToChangeTicks.forEach((p, tB) -> {
                fontRendererObj.drawString(bold + p.name(), 5 + (((width + 40) / 2) - fontRendererObj.getStringWidth(bold + p.name())) / 2, tB.yPosition + 5, 0xFFFFFF);
                tB.drawTickBox();
            });

            fontRendererObj.drawString(bold + "Estate Permissions", (width - fontRendererObj.getStringWidth(bold + "Estate Permissions")) / 2, estateY, 0xFFFFFF);

            if (estateTicks.isEmpty()) {
                fontRendererObj.drawString(red + bold + "None", (width - fontRendererObj.getStringWidth(bold + red + "None")) / 2, estateY + 20, 0xFFFFFF);
            }

            estateTicks.forEach((p, tB) -> {
                fontRendererObj.drawString(bold + p.name(), 5 + (((width + 40) / 2) - fontRendererObj.getStringWidth(bold + p.name())) / 2, tB.yPosition + 5, 0xFFFFFF);
                tB.drawTickBox();
            });

            rentBtn.drawButton(mc, mouseX, mouseY);
            purchaseBtn.drawButton(mc, mouseX, mouseY);
        }

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
            if(purchaseBtn.mousePressed(mc, mouseX, mouseY)) {
                Minelife.NETWORK.sendToServer(new PacketPurchaseEstate(estateID, false));
            } else if (rentBtn.mousePressed(mc, mouseX, mouseY)) {
                Minelife.NETWORK.sendToServer(new PacketPurchaseEstate(estateID, true));
            }
        }

        @Override
        public void drawBackground() {

        }

        @Override
        public void drawSelectionBox(int index, int width, int height) {

        }
    }

}
