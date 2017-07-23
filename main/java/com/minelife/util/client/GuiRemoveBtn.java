package com.minelife.util.client;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiRemoveBtn extends GuiButton {

    private ResourceLocation texture = new ResourceLocation(Minelife.MOD_ID, "textures/gui/x.png");

    public GuiRemoveBtn(int x, int y)
    {
        super(0, x, y, 16, 16, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_BLEND);
        mc.getTextureManager().bindTexture(texture);
        float scale = 1f;

        if (mouseX >= xPosition && mouseX <= xPosition + 16 && mouseY >= yPosition && mouseY <= yPosition + 16) {
            scale = 1.25f;
        }

        GL11.glPushMatrix();
        {
            GL11.glTranslatef(xPosition, yPosition, zLevel);
            if (scale > 1f) {
                GL11.glTranslatef(8, 8, 0);
                GL11.glScalef(scale, scale, scale);
                GL11.glTranslatef(-8, -8, 0);
            }
            GuiUtil.drawImage(0, 0, 16, 16);
        }
        GL11.glPopMatrix();
    }
}
