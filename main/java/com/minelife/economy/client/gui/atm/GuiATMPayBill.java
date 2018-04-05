package com.minelife.economy.client.gui.atm;

import com.minelife.Minelife;
import com.minelife.economy.Bill;
import com.minelife.economy.network.PacketPayBill;
import com.minelife.util.NumberConversions;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiATMPayBill extends GuiATMWithdraw {

    private Bill bill;

    public GuiATMPayBill(long balance, Bill bill) {
        super(balance);
        this.bill = bill;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(new GuiATMBills(balance));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
//        super.actionPerformed(button);
        if (NumberConversions.isInt(button.displayString)) {
            if (NumberConversions.isInt(String.valueOf(amount) + button.displayString)) {
                if (NumberConversions.toInt(String.valueOf(amount) + button.displayString) <= balance
                        && NumberConversions.toInt(String.valueOf(amount) + button.displayString) > 0)
                    amount = NumberConversions.toInt(String.valueOf(amount) + button.displayString);
            }
        } else if (button.displayString.equals("<")) {
            if (amount > 0)
                amount = NumberConversions.toInt(String.valueOf(amount).substring(0, String.valueOf(amount).length() - 1));
        } else {
            if (amount > 0 && amount <= balance)
                Minelife.getNetwork().sendToServer(new PacketPayBill(bill.getUniqueID(), amount));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.get(11).displayString = "Pay";
    }

}
