package com.minelife.gun.client.gui;

import com.minelife.util.client.GuiUtil;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiModifyColor extends GuiScreen {

    private int red = 0, green = 0, blue = 0;
    private int xPosition, yPosition;
    private int bgWidth = 200, bgHeight = 200;

    private int grippedColor = -1;

    public GuiModifyColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f) {
        super.drawScreen(mouse_x, mouse_y, f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        // Draw background
        GL11.glColor4d(11.0 / 255.0, 11.0 / 255.0, 11.0 / 255.0, 220.0 / 255.0);
        GuiUtil.drawImage(xPosition, yPosition, bgWidth, bgHeight);

        // Draw tracks
        GL11.glColor4d(1, 1, 1, 1);
        GuiUtil.drawImage(xPosition + 10, yPosition + bgHeight - 60, bgWidth - 20, 10);
        GuiUtil.drawImage(xPosition + 10, yPosition + bgHeight - 40, bgWidth - 20, 10);
        GuiUtil.drawImage(xPosition + 10, yPosition + bgHeight - 20, bgWidth - 20, 10);

        // Draw grips
        double red_x = xPosition + 10 + (red * (bgWidth - 40) / 255);
        double green_x = xPosition + 10 + (green * (bgWidth - 40) / 255);
        double blue_x = xPosition + 10 + (blue * (bgWidth - 40) / 255);
        double red_y = yPosition + bgHeight - 60;
        double green_y = yPosition + bgHeight - 40;
        double blue_y = yPosition + bgHeight - 20;
        GL11.glColor4d(1, 0, 0, 1);
        GuiUtil.drawImage((float) red_x, (float) red_y, 20, 10);
        GL11.glColor4d(0, 1, 0, 1);
        GuiUtil.drawImage((float) green_x, (float) green_y, 20, 10);
        GL11.glColor4d(0, 0, 1, 1);
        GuiUtil.drawImage((float) blue_x, (float) blue_y, 20, 10);

        // Draw color
        GL11.glColor4d((double) red / 255.0, (double) green / 255.0, (double) blue / 255.0, 1.0);
        GuiUtil.drawImage(xPosition + 20, yPosition + 20, 80, 80);

        // Handle adjusting grips
        boolean mouseDown = Mouse.isButtonDown(0);

        if (mouseDown) {
            if (mouse_x >= xPosition && mouse_x <= xPosition + bgWidth - 20) {
                if (((mouse_y >= red_y && mouse_y <= red_y + 10) && grippedColor == -1) || grippedColor == 0) {
                    int value = (int) (((double) (mouse_x - (xPosition + 20)) / (double) (bgWidth - 40) * 255.0));
                    red = value > 255 ? 255 : value < 0 ? 0 : value;
                    grippedColor = 0;
                }
                if (((mouse_y >= green_y && mouse_y <= green_y + 10) && grippedColor == -1) || grippedColor == 1) {
                    int value = (int) (((double) (mouse_x - (xPosition + 20)) / (double) (bgWidth - 40) * 255.0));
                    green = value > 255 ? 255 : value < 0 ? 0 : value;
                    grippedColor = 1;
                }
                if (((mouse_y >= blue_y && mouse_y <= blue_y + 10) && grippedColor == -1) || grippedColor == 2) {
                    int value = (int) (((double) (mouse_x - (xPosition + 20)) / (double) (bgWidth - 40) * 255.0));
                    blue = value > 255 ? 255 : value < 0 ? 0 : value;
                    grippedColor = 2;
                }
            }
        } else {
            grippedColor = -1;
        }
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn) {
        super.mouseClicked(mouse_x, mouse_y, mouse_btn);
    }

    @Override
    public void initGui() {
        super.initGui();
        xPosition = (this.width - bgWidth) / 2;
        yPosition = (this.height - bgHeight) / 2;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }
}
