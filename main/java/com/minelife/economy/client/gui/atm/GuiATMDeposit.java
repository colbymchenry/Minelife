package com.minelife.economy.client.gui.atm;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.network.PacketDepositATM;
import com.minelife.economy.network.PacketWithdrawATM;
import com.minelife.util.NumberConversions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;

public class GuiATMDeposit extends GuiATMWithdraw {

    public GuiATMDeposit(long balance) {
        super(balance);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (NumberConversions.isInt(button.displayString)) {
            if (NumberConversions.isInt(String.valueOf(amount) + button.displayString)) {
                if (NumberConversions.toInt(String.valueOf(amount) + button.displayString) <= ModEconomy.getBalanceInventory(Minecraft.getMinecraft().player)
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
            if (amount > 0 && amount <= ModEconomy.getBalanceInventory(Minecraft.getMinecraft().player))
                Minelife.getNetwork().sendToServer(new PacketDepositATM(NumberConversions.toInt(amount)));
            else {
                playErrorSound();
                submitMessage("Insufficient Funds", 3);
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.get(11).displayString = "Deposit";
    }
}
