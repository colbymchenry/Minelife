package com.minelife.util.client;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiLoadingAnimation extends Gui {

    public int xPosition, yPosition, width, height;
    public ResourceLocation image;
    private float rotation = 0;
    private float speed = 0.8f;
    private Color color;

    public GuiLoadingAnimation(int xPosition, int yPosition, int width, int height, float speed, ResourceLocation image, Color color)
    {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.image = image;
        this.color = color;
    }

    public GuiLoadingAnimation(int xPosition, int yPosition, int width, int height, float speed, ResourceLocation image)
    {
        this(xPosition, yPosition, width, height, speed, new ResourceLocation(Minelife.MOD_ID, "textures/gui/loading.png"), Color.black);
    }



    public GuiLoadingAnimation(int xPosition, int yPosition, int width, int height, float speed, Color color)
    {
        this(xPosition, yPosition, width, height, speed, new ResourceLocation(Minelife.MOD_ID, "textures/gui/loading.png"), color);
    }

    public GuiLoadingAnimation(int xPosition, int yPosition, int width, int height, Color color)
    {
        this(xPosition, yPosition, width, height, 1.8f, new ResourceLocation(Minelife.MOD_ID, "textures/gui/loading.png"), color);
    }


    public GuiLoadingAnimation(int xPosition, int yPosition, int width, int height)
    {
        this(xPosition, yPosition, width, height, 1.8f, new ResourceLocation(Minelife.MOD_ID, "textures/gui/loading.png"));
    }

    public void drawLoadingAnimation() {
        GL11.glPushMatrix();
        {
            GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
            GL11.glTranslatef(xPosition, yPosition, zLevel);
            GL11.glTranslatef(width / 2, height / 2, 0);
            GL11.glRotatef(rotation+=speed, 0, 0, 1);
            GL11.glTranslatef(-(width / 2), -(height / 2), 0);
            Minecraft.getMinecraft().getTextureManager().bindTexture(image);
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, width, height);
            GL11.glColor4f(1, 1, 1, 1);
        }
        GL11.glPopMatrix();
    }

}
