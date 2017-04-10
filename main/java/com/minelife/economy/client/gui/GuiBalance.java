package com.minelife.economy.client.gui;

import com.minelife.Minelife;
import com.minelife.economy.packet.PacketBalanceQuery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.text.DecimalFormat;

public class GuiBalance extends GuiATM {

    private static final DecimalFormat formatter = new DecimalFormat("#,###");
    private long balance = 0;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int scale = (this.width) / this.fontRendererObj.getStringWidth("$" + this.formatter.format(this.balance));
        if(scale > 5) scale = 5;

        this.drawLabel("$" + this.formatter.format(this.balance), this.width / 2, this.height / 2, scale, 0xFF03438D);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.id == 0)
            Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new ButtonATM(0, 2, this.height - 30 - 2, 75, 30, "Cancel", 2.0));
        Minelife.NETWORK.sendToServer(new PacketBalanceQuery());
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}
