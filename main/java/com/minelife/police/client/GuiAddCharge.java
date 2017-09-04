package com.minelife.police.client;

import com.minelife.police.Charge;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiAddCharge extends GuiScreen {

    private GuiCreateTicket guiCreateTicket;
    private GuiTextField descriptionField, countsField, bailField, jailTime;
    private int xPosition, yPosition, bgWidth = 140, bgHeight = 170;
    private Color bgColor = new Color(0, 63, 126, 255);

    public GuiAddCharge(GuiCreateTicket guiCreateTicket) {
        this.guiCreateTicket = guiCreateTicket;
    }

    @Override
    public void initGui() {
        super.initGui();
        xPosition = (width - bgWidth) / 2;
        yPosition = (height - bgHeight) / 2;

        int sectionHeight = bgHeight / 5;
        int yOffset = 10;
        int row = 0;
        descriptionField = new GuiTextField(fontRendererObj, xPosition   + ((bgWidth - 75) / 2), yPosition + yOffset + (sectionHeight * row++) + ((sectionHeight - 10) / 2), 75, 10);
        countsField = new GuiTextField(fontRendererObj, xPosition  + ((bgWidth - 20) / 2), yPosition + yOffset + (sectionHeight * row++) + ((sectionHeight - 10) / 2), 20, 10);
        bailField = new GuiTextField(fontRendererObj, xPosition  + ((bgWidth - 75) / 2), yPosition + yOffset + (sectionHeight * row++) + ((sectionHeight - 10) / 2), 75, 10);
        jailTime = new GuiTextField(fontRendererObj, xPosition + ((bgWidth - 20) / 2), yPosition + yOffset + (sectionHeight * row++) + ((sectionHeight - 10) / 2) + 10, 20, 10);

        super.buttonList.clear();
        buttonList.add(getCustomButton(0, xPosition + 10, yPosition + bgHeight - 20, 30, 13, "Back"));
        buttonList.add(getCustomButton(1, xPosition + bgWidth - 50, yPosition + bgHeight - 20, 40, 13, "Confirm"));

        ((GuiButton) buttonList.get(0)).enabled = false;
        ((GuiButton) buttonList.get(1)).enabled = false;
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawDefaultBackground();
        GuiUtil.drawDefaultBackground(xPosition, yPosition, bgWidth, bgHeight, bgColor);
        super.drawScreen(x, y, f);
        fontRendererObj.setUnicodeFlag(true);
        drawCenteredString(fontRendererObj, "Description", xPosition + (bgWidth / 2), descriptionField.yPosition - 10, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Counts", xPosition + (bgWidth / 2), countsField.yPosition - 10, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Bail", xPosition + (bgWidth / 2), bailField.yPosition - 10, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Jail Time in Minutes", xPosition + (bgWidth / 2), jailTime.yPosition - 20, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "(20 = 1 MC Day)", xPosition + (bgWidth / 2), jailTime.yPosition - 10, 0xFFFFFF);
        fontRendererObj.setUnicodeFlag(false);
        descriptionField.drawTextBox();
        countsField.drawTextBox();
        bailField.drawTextBox();
        jailTime.drawTextBox();
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        if (guiButton.id == 1) {
            guiCreateTicket.chargeList.add(new Charge(Integer.parseInt(countsField.getText()),
                    Integer.parseInt(bailField.getText()), Integer.parseInt(jailTime.getText()), descriptionField.getText()));
        }

        Minecraft.getMinecraft().displayGuiScreen(guiCreateTicket);
    }

    @Override
    protected void mouseClicked(int x, int y, int btn) {
        super.mouseClicked(x, y, btn);
        descriptionField.mouseClicked(x, y, btn);
        countsField.mouseClicked(x, y, btn);
        bailField.mouseClicked(x, y, btn);
        jailTime.mouseClicked(x, y, btn);
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);
        descriptionField.textboxKeyTyped(keyChar, keyCode);

        if (!NumberConversions.isInt(String.valueOf(keyChar)) && keyCode != Keyboard.KEY_BACK) return;

        countsField.textboxKeyTyped(keyChar, keyCode);
        bailField.textboxKeyTyped(keyChar, keyCode);
        jailTime.textboxKeyTyped(keyChar, keyCode);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        descriptionField.updateCursorCounter();
        countsField.updateCursorCounter();
        bailField.updateCursorCounter();
        jailTime.updateCursorCounter();
        ((GuiButton) buttonList.get(0)).enabled = true;
        ((GuiButton) buttonList.get(1)).enabled = !descriptionField.getText().isEmpty() && !countsField.getText().isEmpty() && !bailField.getText().isEmpty() && !jailTime.getText().isEmpty();
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
