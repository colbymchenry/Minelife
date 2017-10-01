package com.minelife.realestate.client.gui;

import com.minelife.realestate.EnumPermission;
import com.minelife.realestate.Estate;
import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Set;

public class GuiPurchaseEstate extends GuiScreen {

    private Estate estate;
    private Set<EnumPermission> per

    @Override
    public void drawScreen(int x, int y, float f)
    {
        super.drawScreen(x, y, f);
    }

    @Override
    protected void keyTyped(char c, int i)
    {
        super.keyTyped(c, i);
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }

    private class GuiContent extends GuiScrollableContent {

        public GuiContent(Minecraft mc, int xPosition, int yPosition, int width, int height)
        {
            super(mc, xPosition, yPosition, width, height);
        }

        @Override
        public int getObjectHeight(int index)
        {
            return 0;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {

        }

        @Override
        public int getSize()
        {
            return 0;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {

        }
    }
}
