package com.minelife.economy.client.gui.atm;

import com.minelife.Minelife;
import com.minelife.economy.network.PacketSendMoneyATM;
import com.minelife.economy.network.PacketWithdrawATM;
import com.minelife.util.NumberConversions;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

import java.io.IOException;
import java.util.UUID;

public class GuiATMSendMoney extends GuiATMWithdraw {

    public GuiATMSendMoney(int balance) {
        super(balance);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (NumberConversions.isInt(button.displayString)) {
            if (NumberConversions.isInt(String.valueOf(amount) + button.displayString)) {
                if (NumberConversions.toInt(String.valueOf(amount) + button.displayString) <= balance)
                    amount = NumberConversions.toInt(String.valueOf(amount) + button.displayString);
            }
        } else if (button.displayString.equals("<")) {
            if (amount > 0)
                amount = NumberConversions.toInt(String.valueOf(amount).substring(0, String.valueOf(amount).length() - 1));
        } else {
            if (amount > 0 && amount <= balance)
                mc.displayGuiScreen(new GuiATMSendMoney2(this.balance, this.amount));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.get(11).displayString = "Next";
    }

    class GuiATMSendMoney2 extends GuiATMBase {

        private GuiTextField playerNameField;
        private int amount;

        public GuiATMSendMoney2(int balance, int amount) {
            super(balance);
            this.amount = amount;
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            super.drawScreen(mouseX, mouseY, partialTicks);
            playerNameField.drawTextBox();
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            super.keyTyped(typedChar, keyCode);
            playerNameField.textboxKeyTyped(typedChar, keyCode);
            playKeyTypeSound();
        }

        @Override
        protected synchronized void actionPerformed(GuiButton button) throws IOException {
            super.actionPerformed(button);
            UUID playerUUID = UUIDFetcher.asyncFetchClient(playerNameField.getText());
            if(playerUUID == null) {
                playErrorSound();
                submitMessage("Player not found", 3);
            } else {
                Minelife.getNetwork().sendToServer(new PacketSendMoneyATM(playerUUID, this.amount));
            }
        }

        @Override
        public void initGui() {
            super.initGui();
            playerNameField = new GuiTextField(0, fontRenderer, 0, 0, 100, 20);
            this.buttonList.add(new ButtonATM(0, this.guiLeft + ((this.xSize - 100) / 2), playerNameField.y + 40, 100, 30, "Send", 2.0));
        }
    }
}
