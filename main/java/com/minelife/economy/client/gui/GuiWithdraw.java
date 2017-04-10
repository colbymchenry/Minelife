package com.minelife.economy.client.gui;

import com.minelife.Minelife;
import com.minelife.economy.packet.PacketWithdraw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiWithdraw extends GuiDeposit {

    @Override
    protected void actionPerformed(GuiButton button) {
        if (pin.length() < 4 && button.id < 10) {
            // make sure this isn't larger than a valid long
            if (validLong(this.amount + button.displayString)) {
                this.amount += button.displayString;
            }
        }

        // hit the cancel button
        if(button.id == 10)
            Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());

        // hit the enter button
        if(button.id == 11)
            Minelife.NETWORK.sendToServer(new PacketWithdraw(Long.parseLong(this.amount)));
    }


}
