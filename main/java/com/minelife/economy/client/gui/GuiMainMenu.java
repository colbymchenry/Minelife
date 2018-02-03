package com.minelife.economy.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;

public class GuiMainMenu extends GuiATM {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawLabel("Select a transaction", this.width / 2, this.block * 2, 2.0, 0xFF03438D);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiTransfer());
                break;
            }
            case 4: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiWithdraw());
                break;
            }
            case 3: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiBalance());
                break;
            }
            case 5: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiBillPay());
                break;
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        int btnWidth = 170, btnHeight = 25;

        int btnX = (this.width / 2) - (btnWidth + 10);
        int btnY = (this.block * 3) + (this.block / 2);

        this.buttonList.add(new ButtonATM(0, btnX, btnY + (btnHeight * 1), btnWidth, btnHeight, "Transfer", 2.0));
        this.buttonList.add(new ButtonATM(3, btnX, btnY + (btnHeight * 3), btnWidth, btnHeight, "Balance Enquiry", 2.0));

        btnX = (this.width / 2) + 10;
        this.buttonList.add(new ButtonATM(4, btnX, btnY + (btnHeight * 1), btnWidth, btnHeight, "Withdraw Money", 2.0));
        this.buttonList.add(new ButtonATM(5, btnX, btnY + (btnHeight * 3), btnWidth, btnHeight, "Bill Pay", 2.0));
    }
}
