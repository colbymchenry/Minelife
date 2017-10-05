package com.minelife.realestate.client.gui;

import com.minelife.realestate.Permission;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.GuiTickBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;
import java.util.Map;

public class GuiPurchaseEstate extends GuiScreen {

    private double purchasePrice, rentPrice;
    private int rentPeriod;
    private List<Permission> globalPerms, renterPerms, ownerPerms, allowedToChangePerms, estatePerms;

    public GuiPurchaseEstate(double purchasePrice, double rentPrice, int rentPeriod, List<Permission> globalPerms,
                             List<Permission> renterPerms, List<Permission> ownerPerms, List<Permission> allowedToChangePerms,
                             List<Permission> estatePerms) {
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
        super.drawScreen(x, y, f);
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    // TODO
    private class GuiContent extends GuiScrollableContent {

        private int totalHeight;
        private Map<Permission, GuiTickBox> globalTicks, renterTicks, ownerTicks, allowedToChangeTicks, estateTicks;

        public GuiContent(Minecraft mc, int xPosition, int yPosition, int width, int height) {
            super(mc, xPosition, yPosition, width, height);
        }

        @Override
        public int getObjectHeight(int index) {
            return totalHeight;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {

        }

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {

        }
    }

}
