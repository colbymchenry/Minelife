package com.minelife.minebay.client.gui;

import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class CustomButton extends GuiButton {

    private FontRenderer fontRenderer;

    public CustomButton(int p_i1020_1_, int p_i1020_2_, int p_i1020_3_, String p_i1020_4_, FontRenderer fontRenderer)
    {
        super(p_i1020_1_, p_i1020_2_, p_i1020_3_, p_i1020_4_);
        this.fontRenderer = fontRenderer;
        this.width = fontRenderer.getStringWidth(p_i1020_4_) + 15;
        this.height = 20;
    }

    @Override
    public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_)
    {
        boolean hovering = mousePressed(p_146112_1_, p_146112_2_, p_146112_3_);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        GuiUtil.drawDefaultBackground(xPosition, yPosition, width, height, !enabled ?  new Color(139, 139, 141) : hovering ? new Color(230, 0, 228) : new Color(203, 0, 203));
        fontRenderer.drawStringWithShadow(this.displayString, xPosition + 1 + (this.width - fontRenderer.getStringWidth(displayString)) / 2, yPosition + 1 + (this.height - fontRenderer.FONT_HEIGHT) / 2, hovering ? 16777120 : 0xFFFFFF);
    }
}