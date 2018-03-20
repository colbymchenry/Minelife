package com.minelife.util.client;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import java.util.Set;

public class GuiPixelArt extends Gui {

    private static ResourceLocation eraser = new ResourceLocation(Minelife.MOD_ID, "textures/gui/eraser.png");

    private boolean erasing = false;
    protected GuiColorPicker colorPicker;
    private Rectangle canvasBounds;
    public Set<Pixel> Pixels = Sets.newTreeSet();
    private int scale;
    private GuiButton eraseBtn;
    private Minecraft mc;


    public void drawScreen(int mouseX, int mouseY) {
        GL11.glPushMatrix();
        {
            GL11.glTranslatef(canvasBounds.getX(), canvasBounds.getY(), zLevel);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glScalef(scale, scale, scale);

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            for (Pixel p : Pixels) {
                int rgb = p.color;
                int red = (rgb >> 16) & 0x000000FF;
                int green = (rgb >> 8) & 0x000000FF;
                int blue = (rgb) & 0x000000FF;
                GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 1f);
                GuiHelper.drawImage(p.x, p.y, 1, 1);
            }
        }
        GL11.glPopMatrix();

        colorPicker.drawScreen(mouseX, mouseY);

        GL11.glColor4f(1, 1, 1, 1);


        if (erasing) {
            eraseBtn.drawButton(mc, eraseBtn.x + 2, eraseBtn.y + 2, 0);
            mc.getTextureManager().bindTexture(eraser);
            GL11.glColor4f(1, 1, 1, 1);
            GuiHelper.drawImage(eraseBtn.x + 2, eraseBtn.y + 2, 16, 16);
            GuiHelper.drawImage(mouseX, mouseY, 16, 16);
        } else {
            eraseBtn.drawButton(mc, mouseX, mouseY, 0);
            GL11.glColor4f(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(eraser);
            GuiHelper.drawImage(eraseBtn.x + 2, eraseBtn.y + 2, 16, 16);
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        int r = (colorPicker.getColor()>>16)&0xFF;
        int g = (colorPicker.getColor()>>8)&0xFF;
        int b = (colorPicker.getColor()>>0)&0xFF;
        GL11.glColor4f(r / 255f, g / 255f, b / 255f, 1f);
        GuiHelper.drawImage(colorPicker.xPosition, colorPicker.yPosition, colorPicker.width, 10);
    }

    public void mouseClicked(int mouseX, int mouseY) {
        if(eraseBtn.mousePressed(mc, mouseX, mouseY)) erasing = !erasing;

        if (mouseX >= canvasBounds.getX() && mouseX <= canvasBounds.getX() + (canvasBounds.getWidth() * scale) - 1 &&
                mouseY >= canvasBounds.getY() && mouseY <= canvasBounds.getY() + (canvasBounds.getHeight() * scale) - 1) {
            Pixel p = new Pixel((mouseX - canvasBounds.getX()) / scale, (mouseY - canvasBounds.getY()) / scale, colorPicker.getColor());
            if (erasing) {
                Pixels.remove(p);
            } else {
                Pixels.remove(p);
                Pixels.add(p);
            }
        }
    }

    public void mouseClickMove(int mouseX, int mouseY) {
        if (mouseX >= canvasBounds.getX() && mouseX <= canvasBounds.getX() + (canvasBounds.getWidth() * scale) - 1 &&
                mouseY >= canvasBounds.getY() && mouseY <= canvasBounds.getY() + (canvasBounds.getHeight() * scale) - 1) {
            Pixel p = new Pixel((mouseX - canvasBounds.getX()) / scale, (mouseY - canvasBounds.getY()) / scale, colorPicker.getColor());
            if (erasing) {
                Pixels.remove(p);
            } else {
                Pixels.remove(p);
                Pixels.add(p);
            }
        }
    }

    public GuiPixelArt(Minecraft mc, int x, int y, int width, int height, int scale, GuiColorPicker colorPicker) {
        this.mc = mc;
        this.scale = scale;
        this.canvasBounds = new Rectangle(x, y, width, height);
        this.colorPicker = colorPicker;
        eraseBtn = new GuiButton(1, colorPicker.xPosition + colorPicker.width + 20, colorPicker.yPosition + 30, 20, 20, "");
        Pixels = Sets.newTreeSet();
    }

    public int X() {
        return canvasBounds.getX();
    }

    public int Y() {
        return canvasBounds.getY();
    }

    public int Width() {
        return canvasBounds.getWidth();
    }

    public int Height() {
        return canvasBounds.getHeight();
    }

    public int Scale() {
        return scale;
    }

    public GuiColorPicker ColorPicker() {
        return colorPicker;
    }
}
