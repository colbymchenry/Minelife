package com.minelife.economy.client.gui.atm;

import com.minelife.util.NumberConversions;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;

public class GuiATMMenu extends GuiATMBase {

    public GuiATMMenu(int balance) {
        super(balance);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.fontRenderer.drawString("Balance: $" + NumberConversions.format(balance), guiLeft + 5, guiTop + 5, 4210752);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.guiLeft + ((this.xSize - 50) / 2), this.guiTop + 100, 50, 20, "Send Money"));
        this.buttonList.add(new GuiButton(1, this.guiLeft + ((this.xSize - 50) / 2), this.guiTop + 130, 50, 20, "Withdraw"));
        this.buttonList.add(new GuiButton(2, this.guiLeft + ((this.xSize - 50) / 2), this.guiTop + 160, 50, 20, "Pay Bills"));
    }
}
