package com.minelife.economy.client.gui.atm;

import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.GuiScreen;

public class GuiATMBase extends GuiScreen {

    public int guiLeft, guiTop, xSize = 176, ySize = 186;
    public int balance;

    public GuiATMBase(int balance) {
        this.balance = balance;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiHelper.drawDefaultBackground(guiLeft, guiTop, xSize, ySize);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }
}
