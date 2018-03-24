package com.minelife.util.client;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiDropDown extends Gui {

    protected static final ResourceLocation arrow_texture = new ResourceLocation(Minelife.MOD_ID, "textures/gui/arrow.png");
    public int x, y, width, height;
    public String[] options;
    public int selected = 0;
    public boolean drop_down_active = false;
    public Color color = new Color(255, 255, 255);
    public Color colorBG = new Color(140, 140, 140);
    public Color colorHover = new Color(255, 255, 0);
    public Color colorHighlight;


    public GuiDropDown(int x, int y, int width, int height, String... options) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.options = options;
        this.colorHighlight = this.color.darker();
    }

    public void draw(Minecraft mc, int mouse_x, int mouse_y) {
        boolean hover = mouse_x >= x && mouse_x <= x + width && mouse_y >= y && mouse_y <= y + height;
        GlStateManager.color(1, 1, 1, 1);
        drawRect(x, y, x + width, y + height, colorBG.hashCode());
        GlStateManager.color(hover ? colorHover.getRed() / 255f : color.getRed() / 255f, hover ? colorHover.getGreen() / 255f : color.getGreen() / 255f, hover ? colorHover.getBlue() / 255f : color.getBlue() / 255f, 1);
        mc.getTextureManager().bindTexture(arrow_texture);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + width - 10, y + ((height - 8) / 2), 0);
        GlStateManager.translate(4, 4, 4);
        GlStateManager.rotate(drop_down_active ? 180 : 0, 0, 0, 1);
        GlStateManager.translate(-4, -4, -4);
        GuiHelper.drawRect(0, 0, 8, 8);
        GlStateManager.popMatrix();

        mc.fontRenderer.drawString(options[selected], x + 2, y + ((height - mc.fontRenderer.FONT_HEIGHT) / 2) + 1, hover ? colorHover.hashCode() : color.hashCode());

        if (drop_down_active) {
           GlStateManager.color(1, 1, 1, 1);
            drawRect(x, y + height, x + width, y + height + ((options.length - 1) * (mc.fontRenderer.FONT_HEIGHT + 5) + 1), colorBG.hashCode());
            int y = this.y + 3;
            for (int i = 0; i < options.length; i++) {
                if (i != selected) {
                    y += mc.fontRenderer.FONT_HEIGHT + 5;
                    boolean hovering = mouse_x >= x && mouse_x <= x + width && mouse_y >= y && mouse_y <= y + mc.fontRenderer.FONT_HEIGHT;
                    if (hovering)
                        drawRect(x, y - 3, x + width, y + mc.fontRenderer.FONT_HEIGHT + 2, colorHighlight.hashCode());
                    mc.fontRenderer.drawString(options[i], x + 2, y, hovering ? colorHover.hashCode() : color.hashCode());
                }
            }
        }
    }

    public boolean mouseClicked(Minecraft mc, int mouse_x, int mouse_y) {
        if (mouse_x >= x && mouse_x <= x + width) {
            if (mouse_y >= y && mouse_y <= y + height) {
                drop_down_active = !drop_down_active;
                return true;
            } else if (mouse_y >= y && drop_down_active) {
                int y = this.y + 3;
                for (int i = 0; i < options.length; i++) {
                    if (i != selected) {
                        y += mc.fontRenderer.FONT_HEIGHT + 5;
                        boolean hovering = mouse_x >= x && mouse_x <= x + width && mouse_y >= y && mouse_y <= y + mc.fontRenderer.FONT_HEIGHT;
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
