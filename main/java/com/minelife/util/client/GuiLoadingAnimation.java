package com.minelife.util.client;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiLoadingAnimation extends Gui {

    public int xPosition, yPosition, width, height;
    public ResourceLocation image;
    private float rotation = 0;
    private float speed = 0.8f;

    public GuiLoadingAnimation(int xPosition, int yPosition, int width, int height, float speed, ResourceLocation image)
    {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.image = image;
    }

    public GuiLoadingAnimation(int xPosition, int yPosition, int width, int height, float speed)
    {
        this(xPosition, yPosition, width, height, speed, new ResourceLocation(Minelife.MOD_ID, "textures/gui/loading.png"));
    }

    public GuiLoadingAnimation(int xPosition, int yPosition, int width, int height)
    {
        this(xPosition, yPosition, width, height, 1.8f, new ResourceLocation(Minelife.MOD_ID, "textures/gui/loading.png"));
    }

    public void drawLoadingAnimation() {
        GL11.glPushMatrix();
        {
            GL11.glTranslatef(xPosition, yPosition, zLevel);
            GL11.glTranslatef(width / 2, height / 2, 0);
            GL11.glRotatef(rotation+=speed, 0, 0, 1);
            GL11.glTranslatef(-(width / 2), -(height / 2), 0);
            Minecraft.getMinecraft().getTextureManager().bindTexture(image);
            GuiUtil.drawImage(0, 0, width, height);
        }
        GL11.glPopMatrix();
    }

}
