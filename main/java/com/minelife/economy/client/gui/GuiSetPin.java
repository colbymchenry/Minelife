package com.minelife.economy.client.gui;

import com.minelife.Minelife;
import com.minelife.economy.packet.PacketSetPin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiSetPin extends GuiUnlock {

    private boolean cameFromMainMenu = false;

    public GuiSetPin(boolean cameFromMainMenu) {
        this.cameFromMainMenu = cameFromMainMenu;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(pin.length() < 4 && button.id < 10)
            pin += button.displayString;

        // hit the cancel button
        if(button.id == 10) {
            if(this.cameFromMainMenu)
                Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
            else
                mc.thePlayer.closeScreen();
        }

        // hit the enter button
        if(button.id == 11)
            Minelife.NETWORK.sendToServer(new PacketSetPin(this.pin));
    }

    @Override
    public void initGui() {
        super.initGui();
        ((GuiButton)this.buttonList.get(11)).displayString = "Set Pin";
    }
}
