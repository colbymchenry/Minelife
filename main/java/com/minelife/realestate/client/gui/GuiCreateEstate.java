package com.minelife.realestate.client.gui;

import com.minelife.realestate.Permission;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.GuiTickBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Mouse;

import java.util.List;

// TODO: Check if they can create an estate with the current seleciton before opening this GUI
public class GuiCreateEstate extends GuiScreen {

    private List<Permission> playerPermissions;
    private int bgWidth = 200, bgHeight = 200;

    public GuiCreateEstate(List<Permission> playerPermissions) {
        this.playerPermissions = playerPermissions;
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

    private class GuiContent extends GuiScrollableContent {
        private GuiTextField purchaseField, rentField, rentPeriodField, introField, outroField;
        private GuiTickBox rentTickBox;
        private List<GuiTickBox> globalPermissions, renterPermissions, ownerPermissions;

        public GuiContent(Minecraft mc, int xPosition, int yPosition, int width, int height) {
            super(mc, xPosition, yPosition, width, height);

            int y = 0;
            purchaseField = new GuiTextField(fontRendererObj, (bgWidth - 100) / 2, y += 40, 100, 20);
            rentField = new GuiTextField(fontRendererObj, (bgWidth - 100) / 2, y += 40, 100, 20);
            rentPeriodField = new GuiTextField(fontRendererObj, (bgWidth - 100) / 2, y += 40, 100, 20);
            introField = new GuiTextField(fontRendererObj, (bgWidth - 100) / 2, y += 40, 100, 20);
            outroField = new GuiTextField(fontRendererObj, (bgWidth - 100) / 2, y += 40, 100, 20);
            rentTickBox = new GuiTickBox(mc, (bgWidth - GuiTickBox.WIDTH) / 2, y += 40, false);
            for (Permission p : playerPermissions)
                globalPermissions.add(new GuiTickBox(mc, (bgWidth / 2) + (((bgWidth / 2) - GuiTickBox.WIDTH) / 2), y += 40, false));
            for (Permission p : playerPermissions)
                renterPermissions.add(new GuiTickBox(mc, (bgWidth / 2) + (((bgWidth / 2) - GuiTickBox.WIDTH) / 2), y += 40, false));
            for (Permission p : playerPermissions)
                ownerPermissions.add(new GuiTickBox(mc, (bgWidth / 2) + (((bgWidth / 2) - GuiTickBox.WIDTH) / 2), y += 40, false));
        }

        @Override
        public int getObjectHeight(int index) {
            return 400;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            int dWheel = Mouse.getDWheel();
        }

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
            purchaseField.mouseClicked(mouseX, mouseY, 0);
            rentField.mouseClicked(mouseX, mouseY, 0);
            rentPeriodField.mouseClicked(mouseX, mouseY, 0);
            introField.mouseClicked(mouseX, mouseY, 0);
            outroField.mouseClicked(mouseX, mouseY, 0);
            rentTickBox.mouseClicked(mouseX, mouseY);
            globalPermissions.forEach(tB -> tB.mouseClicked(mouseX, mouseY));
            renterPermissions.forEach(tB -> tB.mouseClicked(mouseX, mouseY));
            ownerPermissions.forEach(tB -> tB.mouseClicked(mouseX, mouseY));
        }
    }

}
