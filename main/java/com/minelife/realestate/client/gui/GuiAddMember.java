package com.minelife.realestate.client.gui;

import com.minelife.Minelife;
import com.minelife.realestate.network.PacketAddMember;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiAddMember extends GuiScreen {

    private GuiScreen prevScreen;
    private GuiTextField nameField;
    private GuiButton addBtn, cancelBtn;
    private int bgWidth = 100, bgHeight = 70;
    private int xPos, yPos;
    private int estateID;

    public GuiAddMember(GuiScreen prevScreen, int estateID) {
        this.prevScreen = prevScreen;
        this.estateID = estateID;
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawScreen(x, y, f);
        drawDefaultBackground();
        GuiUtil.drawDefaultBackground(xPos, yPos, bgWidth, bgHeight);
        nameField.drawTextBox();
        addBtn.drawButton(mc, x, y);
        cancelBtn.drawButton(mc, x, y);
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);
        nameField.textboxKeyTyped(keyChar, keyCode);
    }

    @Override
    protected void mouseClicked(int x, int y, int btn) {
        super.mouseClicked(x, y, btn);
        nameField.mouseClicked(x, y, btn);
        if(addBtn.mousePressed(mc, x, y)) {
            Minelife.NETWORK.sendToServer(new PacketAddMember(nameField.getText(), estateID));
        } else if (cancelBtn.mousePressed(mc, x, y)) {
            Minecraft.getMinecraft().displayGuiScreen(prevScreen);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        xPos = (this.width - bgWidth) / 2;
        yPos = (this.height - bgHeight) / 2;
        int sectionW = bgWidth / 2;
        nameField = new GuiTextField(fontRendererObj, xPos + (bgWidth - (bgWidth - 20)) / 2, yPos + 10, bgWidth - 20, 20);
        addBtn = new GuiButton(0, xPos + ((sectionW - 40) / 2) + 2, yPos + 40, 40, 20, "Add");
        cancelBtn = new GuiButton(1, xPos + (((sectionW * 3) - 40) / 2) - 2, yPos + 40, 40, 20, "Cancel");
        addBtn.enabled = false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        nameField.updateCursorCounter();
        addBtn.enabled = !nameField.getText().isEmpty();
    }
}
