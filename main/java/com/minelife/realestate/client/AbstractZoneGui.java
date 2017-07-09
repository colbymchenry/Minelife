package com.minelife.realestate.client;

import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

public class AbstractZoneGui extends GuiScreen {

    protected int bgWidth, bgHeight;
    protected int xPosition, yPosition;

    public AbstractZoneGui(int bgWidth, int bgHeight)
    {
        this.bgWidth = bgWidth;
        this.bgHeight = bgHeight;
    }

    public AbstractZoneGui()
    {
        this(128, 64);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        xPosition = (this.width - bgWidth) / 2;
        yPosition = (this.height - bgHeight) / 2;
    }

    protected void drawBackground()
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(20f / 255f, 20f / 255f, 20f / 255f, 220f / 255f);
        this.drawTexturedModalRect(xPosition, yPosition, 0, 0, bgWidth, bgHeight);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    protected void drawHint(int mouseX, int mouseY, String text)
    {
        int textWidth = fontRendererObj.getStringWidth(text);
        int boxWidth = textWidth + 4;

        int x = mouseX + 5;
        int y = mouseY - 10;

        // will flip to other side of mouse if falls out of screen
        x = x + boxWidth > this.width ? x - boxWidth : x;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(0, 0, 0, 1);
        this.drawTexturedModalRect(x, y, 0, 0, boxWidth, fontRendererObj.FONT_HEIGHT + 2);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        fontRendererObj.drawString(text, x + 2, y + 2, 0xFFFFFF, false);
    }

    protected int calcX(int width)
    {
        return this.xPosition + ((this.bgWidth - width) / 2);
    }

    protected int calcY(int height)
    {
        return this.yPosition + ((this.bgHeight - height) / 2);
    }

}