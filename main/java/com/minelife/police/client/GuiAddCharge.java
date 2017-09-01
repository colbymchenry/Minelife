package com.minelife.police.client;

import com.minelife.police.Charge;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

public class GuiAddCharge extends GuiScreen {

    private GuiCreateTicket guiCreateTicket;
    private GuiTextField descriptionField, countsField, bailField, jailTime;
    private int xPosition, yPosition, bgWidth = 140, bgHeight = 200;

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
        descriptionField = new GuiTextField(fontRendererObj, xPosition   + ((bgWidth - 75) / 2), yPosition + yOffset + (sectionHeight * row++) + ((sectionHeight - 20) / 2), 75, 20);
        countsField = new GuiTextField(fontRendererObj, xPosition  + ((bgWidth - 20) / 2), yPosition + yOffset + (sectionHeight * row++) + ((sectionHeight - 20) / 2), 20, 20);
        bailField = new GuiTextField(fontRendererObj, xPosition  + ((bgWidth - 75) / 2), yPosition + yOffset + (sectionHeight * row++) + ((sectionHeight - 20) / 2), 75, 20);
        jailTime = new GuiTextField(fontRendererObj, xPosition + ((bgWidth - 20) / 2), yPosition + yOffset + (sectionHeight * row++) + ((sectionHeight - 20) / 2) + 10, 20, 20);

        super.buttonList.clear();
        buttonList.add(new GuiButton(0, xPosition + 10, yPosition + bgHeight - 20, 30, 20, "Back"));
        buttonList.add(new GuiButton(1, xPosition + bgWidth - 50, yPosition + bgHeight - 20, 50, 20, "Confirm"));

        ((GuiButton) buttonList.get(0)).enabled = false;
        ((GuiButton) buttonList.get(1)).enabled = false;
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawDefaultBackground();
        GuiUtil.drawDefaultBackground(xPosition, yPosition, bgWidth, bgHeight);
        super.drawScreen(x, y, f);
        drawCenteredString(fontRendererObj, "Description", xPosition + (bgWidth / 2), descriptionField.yPosition - 10, 0xFFFFFF);
        descriptionField.drawTextBox();
        drawCenteredString(fontRendererObj, "Counts", xPosition + (bgWidth / 2), countsField.yPosition - 10, 0xFFFFFF);
        countsField.drawTextBox();
        drawCenteredString(fontRendererObj, "Bail", xPosition + (bgWidth / 2), bailField.yPosition - 10, 0xFFFFFF);
        bailField.drawTextBox();
        drawCenteredString(fontRendererObj, "Jail Time in Minutes", xPosition + (bgWidth / 2), jailTime.yPosition - 20, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "(20 = 1 MC Day)", xPosition + (bgWidth / 2), jailTime.yPosition - 10, 0xFFFFFF);
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

}
