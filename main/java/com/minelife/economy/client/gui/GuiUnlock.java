package com.minelife.economy.client.gui;

import com.minelife.Minelife;
import com.minelife.economy.packet.PacketVerifyPin;
import net.minecraft.client.gui.GuiButton;

public class GuiUnlock extends GuiATM {

    protected String pin = "";

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        String asterisks = "";
        for (int i = 0; i < pin.length(); i++)
            asterisks += "*";

        this.drawLabel(asterisks, this.width / 2, this.block * 2, 5.0, 0xFF03438D);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(pin.length() < 4 && button.id < 10)
            pin += button.displayString;

        // hit the cancel button
        if(button.id == 10)
            mc.thePlayer.closeScreen();

        // hit the enter button
        if(button.id == 11)
            Minelife.NETWORK.sendToServer(new PacketVerifyPin(this.pin));

    }

    @Override
    public void initGui() {
        super.initGui();
        this.setupNumPad();

        this.buttonList.add(new ButtonATM(10, 2, this.height - 30 - 2, 75, 30, "Cancel", 2.0));
        this.buttonList.add(new ButtonATM(11, this.width - 75 - 2, this.height - 30 - 2, 75, 30, "Enter", 2.0));
    }

    protected void setupNumPad() {
        int btnWidth = 30, btnHeight = 30;
        int btnBoxWidth = (btnWidth + 5) * 3, btnBoxHeight = (btnHeight + 5) * 4;
        int btnCellWidth = btnBoxWidth / 3, btnCellHeight = btnBoxHeight / 4;
        int btnX = this.width / 2;
        btnX -= btnBoxWidth /2;
        int btnY = this.block * 4;
        btnY -= btnBoxHeight /2;

        btnY += this.block;

        this.buttonList.add(new ButtonATM(0, btnX + btnCellWidth * 0, btnY + btnCellHeight * 0, btnWidth, btnHeight, "1", 2.0));
        this.buttonList.add(new ButtonATM(1, btnX + btnCellWidth * 1, btnY + btnCellHeight * 0, btnWidth, btnHeight, "2", 2.0));
        this.buttonList.add(new ButtonATM(2, btnX + btnCellWidth * 2, btnY + btnCellHeight * 0, btnWidth, btnHeight, "3", 2.0));
        this.buttonList.add(new ButtonATM(3, btnX + btnCellWidth * 0, btnY + btnCellHeight * 1, btnWidth, btnHeight, "4", 2.0));
        this.buttonList.add(new ButtonATM(4, btnX + btnCellWidth * 1, btnY + btnCellHeight * 1, btnWidth, btnHeight, "5", 2.0));
        this.buttonList.add(new ButtonATM(5, btnX + btnCellWidth * 2, btnY + btnCellHeight * 1, btnWidth, btnHeight, "6", 2.0));
        this.buttonList.add(new ButtonATM(6, btnX + btnCellWidth * 0, btnY + btnCellHeight * 2, btnWidth, btnHeight, "7", 2.0));
        this.buttonList.add(new ButtonATM(7, btnX + btnCellWidth * 1, btnY + btnCellHeight * 2, btnWidth, btnHeight, "8", 2.0));
        this.buttonList.add(new ButtonATM(8, btnX + btnCellWidth * 2, btnY + btnCellHeight * 2, btnWidth, btnHeight, "9", 2.0));
        this.buttonList.add(new ButtonATM(9, btnX + btnCellWidth * 1, btnY + btnCellHeight * 3, btnWidth, btnHeight, "0", 2.0));
    }

}
