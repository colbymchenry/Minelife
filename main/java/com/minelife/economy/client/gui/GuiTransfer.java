package com.minelife.economy.client.gui;

import com.minelife.Minelife;
import com.minelife.economy.packet.PacketTransferMoney;
import com.minelife.util.NumberConversions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiTransfer extends GuiDeposit {

    @Override
    protected void actionPerformed(GuiButton button) {
        if (pin.length() < 4 && button.id < 10) {
            // make sure this isn't larger than a valid long
            if (NumberConversions.isDouble(this.amount + button.displayString)) {
                this.amount += button.displayString;
            }
        }

        // hit the cancel button
        if (button.id == 10)
            Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());

        // hit the enter button
        if (button.id == 11)
            Minecraft.getMinecraft().displayGuiScreen(new Stage2(Long.parseLong(this.amount)));
    }

    private class Stage2 extends GuiATM {

        private long amount;
        private String player = "";
        private int counter = 0;

        public Stage2(long amount) {
            this.amount = amount;
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            super.drawScreen(mouseX, mouseY, partialTicks);

            // we check if the player is string is empty to eliminate the chance of dividing by zero
            int scale = this.width / this.fontRendererObj.getStringWidth(this.player.isEmpty() ? "." : this.player);
            if (scale > 5) scale = 5;

            drawLabel(this.player + (this.counter / 6 % 2 == 0 ? "|" : ""), this.width / 2, this.height / 2, scale, 0xFF03438D);
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) {
            super.keyTyped(typedChar, keyCode);
            if (keyCode == Keyboard.KEY_BACK) {
                if (this.player.length() > 0)
                    this.player = this.player.substring(0, this.player.length() - 1);
            } else {
                Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher("" + typedChar);

                /**
                 * We are checking for special characters
                 */
                if (!m.find())
                    this.player += typedChar;
            }
        }

        @Override
        protected void actionPerformed(GuiButton button) {
            if(button.id == 0) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
            } else if (button.id == 1) {
                Minelife.NETWORK.sendToServer(new PacketTransferMoney(this.player, this.amount));
            }
        }

        @Override
        public void initGui() {
            super.initGui();

            this.buttonList.add(new ButtonATM(0, 2, this.height - 30 - 2, 75, 30, "Cancel", 2.0));
            this.buttonList.add(new ButtonATM(1, this.width - 75 - 2, this.height - 30 - 2, 75, 30, "Enter", 2.0));
        }

        @Override
        public void updateScreen() {
            this.counter = this.counter + 1 >= Integer.MAX_VALUE ? 0 : this.counter + 1;

            /**
             * If the transfer is successful, send them to the main menu
             */
            if(this.statusMessage.equalsIgnoreCase("Success.")) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
            }
        }
    }

}

