package com.minelife.realestate.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

public class CustomZoneBtn extends GuiButton {

    public CustomZoneBtn(int id, int xPosition, int yPosition, int width, int height, String label)
    {
        super(id, xPosition, yPosition, width, height, label);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);

        if (this.mousePressed(mc, mouseX, mouseY))
            GL11.glColor4f(80f / 255f, 80f / 255f, 80f / 255f, 220f / 255f);
        else
            GL11.glColor4f(20f / 255f, 20f / 255f, 20f / 255f, 220f / 255f);

        this.drawTexturedModalRect(xPosition, yPosition, 0, 0, width, height);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        mc.fontRenderer.drawString(displayString, xPosition + ((width - mc.fontRenderer.getStringWidth(displayString)) / 2), yPosition + ((height - mc.fontRenderer.FONT_HEIGHT) / 2) + 1, 0xFFFFFF);
    }
}
