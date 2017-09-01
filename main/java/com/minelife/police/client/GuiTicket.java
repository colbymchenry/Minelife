package com.minelife.police.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

public class GuiTicket extends GuiScreen {

    private ItemStack ticketStack;

    public GuiTicket(ItemStack ticketStack) {
        this.ticketStack = ticketStack;
    }


    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawScreen(x, y, f);
        int w = 100, h = 100;
        int posX = (this.width - 100) / 2;
        int posY = (this.height - 100) /2;
    }

    @Override
    protected void mouseClicked(int x, int y, int btn) {
        super.mouseClicked(x, y, btn);
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }
}
