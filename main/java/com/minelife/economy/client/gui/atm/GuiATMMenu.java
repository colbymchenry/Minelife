package com.minelife.economy.client.gui.atm;

import com.minelife.util.NumberConversions;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;

public class GuiATMMenu extends GuiATMBase {

    public GuiATMMenu(long balance) {
        super(balance);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawLabel("Balance: $" + NumberConversions.format(balance), 5, height - 18, 2.0, 0xf4424b, false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 0:

                break;
            case 1:
                mc.displayGuiScreen(new GuiATMWithdraw(this.balance));
                break;
            case 2:

                break;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new ButtonATM(0, this.guiLeft + ((this.xSize - 150) / 2), this.guiTop + 40, 150, 30, "Send Money", 2.0));
        this.buttonList.add(new ButtonATM(1, this.guiLeft + ((this.xSize - 150) / 2), this.buttonList.get(0).y + 50, 150, 30, "Withdraw", 2.0));
        this.buttonList.add(new ButtonATM(2, this.guiLeft + ((this.xSize - 150) / 2), this.buttonList.get(1).y + 50, 150, 30, "Pay Bills", 2.0));
    }
}
