package com.minelife.police.client.gui.ticket;

import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import java.util.List;

public class GuiTicketSearch extends GuiScreen {

    public GuiTicketSearch() {

    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawScreen(x, y, f);
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);
    }

    @Override
    protected void mouseClicked(int x, int y, int btn) {
        super.mouseClicked(x, y, btn);
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    private class GuiResults extends GuiScrollableContent {

        public GuiResults(int xPosition, int yPosition, int width, int height) {
            super(xPosition, yPosition, width, height);
        }

        @Override
        public int getObjectHeight(int index) {
            return 0;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {

        }

        @Override
        public int getSize() {
            return 0;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {

        }
    }


}
