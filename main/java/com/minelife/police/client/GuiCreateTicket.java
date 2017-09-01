package com.minelife.police.client;

import com.google.common.collect.Lists;
import com.minelife.police.Charge;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class GuiCreateTicket extends GuiScreen {

    private ItemStack ticketStack;

    private int xPosition, yPosition, bgWidth = 200, bgHeight = 235;
    private GuiTextField playerField;
    public List<Charge> chargeList;
    private GuiChargeList guiDefaultList, guiChargeList;
    private Color bgColor = new Color(0, 63, 126, 255);

    public GuiCreateTicket(ItemStack ticketStack) {
        this.ticketStack = ticketStack;
        this.chargeList = Lists.newArrayList();
    }

    @Override
    public void initGui() {
        super.initGui();
        xPosition = (width - bgWidth) / 2;
        yPosition = (height - bgHeight) / 2;
        playerField = new GuiTextField(fontRendererObj, xPosition + 40, yPosition + 10, 75, 10);
        super.buttonList.clear();
        buttonList.add(getCustomButton(0, xPosition + bgWidth + 5, yPosition + 35, 16, 15, "+"));
        buttonList.add(getCustomButton(1, xPosition + bgWidth - 42, yPosition + 8, 35, 15, "Submit"));
        guiChargeList = new GuiChargeList(xPosition + 5, yPosition + 40, bgWidth - 10, (bgHeight / 2) - 30, chargeList);
        guiDefaultList = new GuiChargeList(xPosition + 5, guiChargeList.yPosition + guiChargeList.height + 15, bgWidth - 10, (bgHeight / 2) - 30, Charge.getDefaultCharges()) {

            @Override
            public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
                if (doubleClick) guiChargeList.chargeList.add(chargeList.get(index));
            }
        };

        guiChargeList.unicodeFlag = true;
        guiDefaultList.unicodeFlag = true;
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawDefaultBackground();
        int dWheel = Mouse.getDWheel();


        GuiUtil.drawDefaultBackground(xPosition, yPosition, bgWidth, bgHeight, bgColor);
        playerField.drawTextBox();
        guiChargeList.draw(x, y, dWheel);
        guiDefaultList.draw(x, y, dWheel);

        fontRendererObj.setUnicodeFlag(true);
        drawString(fontRendererObj, "Player:", xPosition + 10, yPosition + 10, 0xFFFFFF);
        drawString(fontRendererObj, "Charges:", guiChargeList.xPosition, guiChargeList.yPosition - 10, 0xFFFFFF);
        drawString(fontRendererObj, "Common Charges:", guiDefaultList.xPosition, guiDefaultList.yPosition - 10, 0xFFFFFF);
        fontRendererObj.setUnicodeFlag(false);

        super.drawScreen(x, y, f);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        if (guiButton.id == 0) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiAddCharge(this));
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int btn) {
        super.mouseClicked(x, y, btn);
        playerField.mouseClicked(x, y, btn);
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);
        playerField.textboxKeyTyped(keyChar, keyCode);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        playerField.updateCursorCounter();
    }

    public GuiButton getCustomButton(int id, int x, int y, int width, int height, String text) {
        return new GuiButton(id, x, y, width, height, text) {
            Color c1 = new Color(0, 127, 220, 128);
            Color c2 = new Color(0, 40, 81, 184);

            @Override
            public void drawButton(Minecraft mc, int x, int y) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glColor4f(c1.getRed() / 255f, c1.getGreen() / 255f, c1.getBlue() / 255f, c1.getAlpha() / 255f);
                GuiUtil.drawImage(this.xPosition, this.yPosition, this.width, this.height);
                GL11.glColor4f(c2.getRed() / 255f, c2.getGreen() / 255f, c2.getBlue() / 255f, c2.getAlpha() / 255f);
                GuiUtil.drawImage(this.xPosition + 2, this.yPosition + 2, this.width - 4, this.height - 4);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glColor4f(1, 1, 1, 1);
                boolean hovered = this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
                int l = !this.enabled ? 10526880 : hovered ? 16777120 : 14737632;
                fontRendererObj.setUnicodeFlag(true);
                this.drawCenteredString(fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
                fontRendererObj.setUnicodeFlag(false);
            }
        };
    }

}
