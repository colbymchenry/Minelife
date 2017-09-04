package com.minelife.police.client;

import com.minelife.police.Charge;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.List;

public class GuiTicket extends GuiScreen {

    private ItemStack ticketStack;
    private Color bgColor = new Color(0, 63, 126, 255);
    private GuiChargeList guiChargeList;
    private List<Charge> chargeList;
    private int xPosition, yPosition;
    private int width = 200, height = 235;

    public GuiTicket(ItemStack ticketStack) {
        this.ticketStack = ticketStack;
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawScreen(x, y, f);
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

    @Override
    public void initGui() {
        super.initGui();
    }
}
