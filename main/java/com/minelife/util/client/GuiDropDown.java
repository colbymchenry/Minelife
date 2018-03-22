package com.minelife.util.client;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiDropDown extends Gui {

    protected static final ResourceLocation arrow_texture = new ResourceLocation(Minelife.MOD_ID, "textures/gui/arrow.png");
    public int xPosition, yPosition, width, height;
    public String[] options;
    public int selected = 0;
    public boolean drop_down_active = false;
    public Color color = new Color(255, 255, 255);
    public Color color_bg = new Color(140, 140, 140);
    public Color color_hover = new Color(255, 255, 0);
    public Color color_highlight = color_bg;


    public GuiDropDown(int xPosition, int yPosition, int width, int height, String... options) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
        this.options = options;
        this.color_highlight = this.color_highlight.darker();
    }

    public void draw(Minecraft mc, int mouse_x, int mouse_y) {
        boolean hover = mouse_x >= xPosition && mouse_x <= xPosition + width && mouse_y >= yPosition && mouse_y <= yPosition + height;
        GL11.glColor4f(1, 1, 1, 1);
        drawRect(xPosition, yPosition, xPosition + width, yPosition + height, color_bg.hashCode());
        GL11.glColor4f(hover ? color_hover.getRed() / 255f : color.getRed() / 255f, hover ? color_hover.getGreen() / 255f : color.getGreen() / 255f, hover ? color_hover.getBlue() / 255f : color.getBlue() / 255f, 1);
        mc.getTextureManager().bindTexture(arrow_texture);

        GL11.glPushMatrix();
        {
            GL11.glTranslatef(xPosition + width - 10, yPosition + ((height - 8) / 2), 0);
            GL11.glTranslatef(4, 4, 4);
            GL11.glRotatef(drop_down_active ? 180 : 0, 0, 0, 1);
            GL11.glTranslatef(-4, -4, -4);
            GuiHelper.drawRect(0, 0, 8, 8);
        }
        GL11.glPopMatrix();

        mc.fontRenderer.drawString(options[selected], xPosition + 2, yPosition + ((height - mc.fontRenderer.FONT_HEIGHT) / 2) + 1, hover ? color_hover.hashCode() : color.hashCode());

        if (drop_down_active) {
            GL11.glColor4f(1, 1, 1, 1);
            drawRect(xPosition, yPosition + height, xPosition + width, yPosition + height + ((options.length - 1) * (mc.fontRenderer.FONT_HEIGHT + 5) + 1), color_bg.hashCode());
            int y = yPosition + 3;
            for (int i = 0; i < options.length; i++) {
                if (i != selected) {
                    y += mc.fontRenderer.FONT_HEIGHT + 5;
                    boolean hovering = mouse_x >= xPosition && mouse_x <= xPosition + width && mouse_y >= y && mouse_y <= y + mc.fontRenderer.FONT_HEIGHT;
                    if (hovering)
                        drawRect(xPosition, y - 3, xPosition + width, y + mc.fontRenderer.FONT_HEIGHT + 2, color_highlight.hashCode());
                    mc.fontRenderer.drawString(options[i], xPosition + 2, y, hovering ? color_hover.hashCode() : color.hashCode());
                }
            }
        }
    }

    public boolean mouseClicked(Minecraft mc, int mouse_x, int mouse_y) {
        if (mouse_x >= xPosition && mouse_x <= xPosition + width) {
            if (mouse_y >= yPosition && mouse_y <= yPosition + height) {
                drop_down_active = !drop_down_active;
                return true;
            } else if (mouse_y >= yPosition && drop_down_active) {
                int y = yPosition + 3;
                for (int i = 0; i < options.length; i++) {
                    if (i != selected) {
                        y += mc.fontRenderer.FONT_HEIGHT + 5;
                        boolean hovering = mouse_x >= xPosition && mouse_x <= xPosition + width && mouse_y >= y && mouse_y <= y + mc.fontRenderer.FONT_HEIGHT;
                        if (hovering) {
                            selected = i;
                            drop_down_active = !drop_down_active;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


}
