package com.minelife.economy.client.gui;

import com.minelife.Minelife;
import com.minelife.economy.packet.PacketDeposit;
import com.minelife.util.NumberConversions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;
import java.text.DecimalFormat;

public class GuiDeposit extends GuiUnlock {

    private static final DecimalFormat formatter = new DecimalFormat("#,###");
    protected String amount = "0";

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int scale = (this.width) / this.fontRendererObj.getStringWidth("$" + this.formatter.format(Double.parseDouble(this.amount)));
        if (scale > 5) scale = 5;

        this.drawLabel("$" + this.formatter.format(Double.parseDouble(this.amount)), this.width / 2, this.block * 2, scale, 0xFF03438D);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (pin.length() < 4 && button.id < 10) {
            // make sure this isn't larger than a valid double
            if (NumberConversions.isDouble(this.amount + button.displayString)) {
                this.amount += button.displayString;
            }
        }

        // hit the cancel button
        if (button.id == 10)
            Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());

        // hit the enter button
        if (button.id == 11)
            Minelife.NETWORK.sendToServer(new PacketDeposit(Double.parseDouble(this.amount)));
    }

    @Override
    public void updateScreen() {
        /**
         * Update all the components if the transaction was successful
         */
        if (this.statusMessage.equalsIgnoreCase("Success.")) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
        }
    }
}
