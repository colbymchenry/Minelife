package com.minelife.economy.client.gui.atm;

import com.minelife.Minelife;
import com.minelife.economy.network.PacketWithdrawATM;
import com.minelife.util.NumberConversions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiATMWithdraw extends GuiATMBase {

    protected int amount = 0;

    public GuiATMWithdraw(long balance) {
        super(balance);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawLabel("$" + NumberConversions.format(amount), width / 2, (height / 2) - 60, 2.0, 0xf4424b, true);
        drawLabel("Balance: $" + NumberConversions.format(balance), 5, height - 18, 2.0, 0xf4424b, false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (NumberConversions.isInt(button.displayString)) {
            if (NumberConversions.isInt(String.valueOf(amount) + button.displayString)) {
                if (NumberConversions.toInt(String.valueOf(amount) + button.displayString) <= balance
                        && NumberConversions.toInt(String.valueOf(amount) + button.displayString) > 0) {
                    amount = NumberConversions.toInt(String.valueOf(amount) + button.displayString);
                } else {
                    playErrorSound();
                    submitMessage("Insufficient Funds", 3);
                }
            } else {
                playErrorSound();
                submitMessage("Amount too large", 3);
            }
        } else if (button.displayString.equals("<")) {
            if (amount > 0)
                amount = NumberConversions.toInt(String.valueOf(amount).substring(0, String.valueOf(amount).length() - 1));
            else
                playErrorSound();
        } else {
            if (amount > 0 && amount <= balance)
                Minelife.getNetwork().sendToServer(new PacketWithdrawATM(NumberConversions.toInt(amount)));
            else {
                playErrorSound();
                submitMessage("Insufficient Funds", 3);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE)
            Minecraft.getMinecraft().displayGuiScreen(new GuiATMMenu(this.balance));

        playKeyTypeSound();
    }

    @Override
    public void initGui() {
        super.initGui();
        setupNumPad();
    }

    protected void setupNumPad() {
        int btnWidth = 30, btnHeight = 30;
        int btnBoxWidth = (btnWidth + 5) * 3, btnBoxHeight = (btnHeight + 5) * 4;
        int btnCellWidth = btnBoxWidth / 3, btnCellHeight = btnBoxHeight / 4;
        int btnX = this.width / 2;
        btnX -= btnBoxWidth / 2;
        int btnY = this.block * 4;
        btnY -= btnBoxHeight / 2;

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
        this.buttonList.add(new ButtonATM(10, btnX + btnCellWidth * 3, btnY + btnCellHeight * 0, btnWidth, btnHeight, "<", 2.0));
        this.buttonList.add(new ButtonATM(11, width - 150, height - 40, 150, 40, "Withdraw", 2.0));
    }

}
