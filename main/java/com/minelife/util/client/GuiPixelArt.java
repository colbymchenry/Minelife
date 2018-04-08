package com.minelife.util.client;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import java.awt.*;
import java.util.Set;

public class GuiPixelArt extends Gui {

    private static ResourceLocation eraser = new ResourceLocation(Minelife.MOD_ID, "textures/gui/eraser.png");
    private static ResourceLocation colorpicker = new ResourceLocation(Minelife.MOD_ID, "textures/gui/colorpicker.png");

    private boolean erasing = false, pickingColor = false, mouseDown = false;
    protected GuiColorPicker colorPicker;
    private Rectangle canvasBounds;
    public Set<Pixel> Pixels;
    private int scale;
    private GuiButton eraseBtn, colorPickerBtn;
    private Minecraft mc;


    public void drawScreen(int mouseX, int mouseY) {

        if(Mouse.isButtonDown(0)) {
            if(!mouseDown) {
                mouseDown = true;
                mouseClicked(mouseX, mouseY);
            }
            mouseClickMove(mouseX, mouseY);
        } else {
            mouseDown = false;
        }

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
                Gui.drawRect(p.x, p.y, p.x + 1, p.y + 1, 0xFFFFFF);
                if(p.x == 21 && p.y == 16) {
                    GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 1f);
                    Gui.drawRect(p.x, p.y, p.x + 1, p.y + 1, 0xFFFFFF);
                }
            }
        }
        GL11.glPopMatrix();

        colorPicker.drawScreen(mouseX, mouseY);

        GL11.glColor4f(1, 1, 1, 1);
        eraseBtn.drawButton(mc, mouseX, mouseY, 0);
        mc.getTextureManager().bindTexture(eraser);
        GuiHelper.drawImage(eraseBtn.x + 2, eraseBtn.y + 2, 16, 16, eraser);
        colorPickerBtn.drawButton(mc, mouseX, mouseY, 0);
        mc.getTextureManager().bindTexture(colorpicker);
        GuiHelper.drawImage(colorPickerBtn.x + 2, colorPickerBtn.y + 2, 16, 16, colorpicker);


        if (erasing) {
            mc.getTextureManager().bindTexture(eraser);
            GuiHelper.drawImage(mouseX, mouseY, 16, 16, eraser);
        }

        if (pickingColor) {
            mc.getTextureManager().bindTexture(colorpicker);
            GuiHelper.drawImage(mouseX, mouseY, 16, 16, colorpicker);
        }

        GlStateManager.disableTexture2D();
        Gui.drawRect(colorPicker.xPosition, colorPicker.yPosition, colorPicker.xPosition + colorPicker.width, colorPicker.yPosition + colorPicker.height, colorPicker.getColor());
    }

    private void mouseClicked(int mouseX, int mouseY) {
        if(eraseBtn.mousePressed(mc, mouseX, mouseY)) {
            pickingColor = false;
            erasing = !erasing;
            return;
        }

        if(colorPickerBtn.mousePressed(mc, mouseX, mouseY)) {
            erasing = false;
            pickingColor = !pickingColor;
            return;
        }
    }

    private void mouseClickMove(int mouseX, int mouseY) {
        if (mouseX > canvasBounds.getX() && mouseX < canvasBounds.getX() + (canvasBounds.getWidth() * scale) &&
                mouseY > canvasBounds.getY() && mouseY < canvasBounds.getY() + (canvasBounds.getHeight() * scale)) {
            Pixel p = new Pixel((int) Math.floor((mouseX - canvasBounds.getX()) / scale), (int) Math.floor((mouseY - canvasBounds.getY()) / scale), colorPicker.getColor());
            if (erasing) {
                Pixels.remove(p);
            } else if(pickingColor) {
                if(Pixels.contains(p)) {
                    for (Pixel pixel : Pixels) {
                        if (pixel.x == p.x && pixel.y == p.y) {
                            p = pixel;
                            break;
                        }
                    }
                    Color c = new Color(p.color);
                    colorPicker.setBlue(c.getBlue());
                    colorPicker.setRed(c.getRed());
                    colorPicker.setGreen(c.getGreen());
                }
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
        colorPickerBtn = new GuiButton(2, eraseBtn.x, eraseBtn.y + 22, 20, 20, "");
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
