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
        switch(button.id) {
            case 0: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiTransfer());
                break;
            }
            case 1: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiDeposit());
                break;
            }
            case 2: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiSetPin(true));
                break;
            }
            case 3: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiBalance());
                break;
            }
            case 4: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiWithdraw());
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

        this.buttonList.add(new ButtonATM(0, btnX, btnY, btnWidth, btnHeight, "Transfer", 2.0));
        this.buttonList.add(new ButtonATM(1, btnX, btnY + (btnHeight * 2), btnWidth, btnHeight, "Deposit Money", 2.0));
        this.buttonList.add(new ButtonATM(2, btnX, btnY + (btnHeight * 4) , btnWidth, btnHeight, "PIN Change", 2.0));

        btnX = (this.width / 2) + 10;
        this.buttonList.add(new ButtonATM(3, btnX, btnY, btnWidth, btnHeight, "Balance Enquiry", 2.0));
        this.buttonList.add(new ButtonATM(4, btnX, btnY + (btnHeight * 2), btnWidth, btnHeight, "Withdraw Money", 2.0));
        this.buttonList.add(new ButtonATM(5, btnX, btnY + (btnHeight * 4) , btnWidth, btnHeight, "Bill Pay", 2.0));
    }
}
