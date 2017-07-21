package com.minelife.util.client;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiLoadingAnimation extends Gui {

    public int xPosition, yPosition, width, height;
    public int imageWidth, imageHeight;
    public ResourceLocation image;
    private float rotation = 0;

    public GuiLoadingAnimation(int xPosition, int yPosition, int width, int height, ResourceLocation image, int imageWidth, int imageHeight)
    {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
        this.image = image;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public GuiLoadingAnimation(int xPosition, int yPosition, int width, int height)
    {
        this(xPosition, yPosition, width, height, new ResourceLocation(Minelife.MOD_ID, "textures/gui/loading.png"), 400, 400);
    }

    public void drawLoadingAnimation() {
        GL11.glPushMatrix();
        {
            GL11.glTranslatef(xPosition, yPosition, zLevel);
            GL11.glTranslatef(imageWidth / 2, imageHeight / 2, 0);
            GL11.glRotatef(rotation+=0.002f, 0, 0, 1);
            GL11.glTranslatef(-(imageWidth / 2), -(imageHeight / 2), 0);
            Minecraft.getMinecraft().getTextureManager().bindTexture(image);
            GuiUtil.drawImage(0, 0, width, height);
        }
        GL11.glPopMatrix();
    }

}
