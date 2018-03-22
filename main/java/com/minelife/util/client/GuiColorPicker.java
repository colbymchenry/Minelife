package com.minelife.util.client;

import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiColorPicker extends Gui {


    private int red = 0, green = 0, blue = 0;
    public int xPosition, yPosition;
    public int width, height;
    private int grippedColor = -1;

    public GuiColorPicker(int xPosition, int yPosition, int width, int height, int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.xPosition = xPosition;
        this.yPosition =yPosition;
        this.width = width;
        this.height = height;
    }

    public void drawScreen(int mouse_x, int mouse_y) {

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        // Draw tracks
        GL11.glColor4d(1, 1, 1, 1);
        GuiHelper.drawRect(xPosition + 10, yPosition + height - 60, width - 20, 10);
        GuiHelper.drawRect(xPosition + 10, yPosition + height - 40, width - 20, 10);
        GuiHelper.drawRect(xPosition + 10, yPosition + height - 20, width - 20, 10);

        // Draw grips
        int red_x = xPosition + 10 + (red * (width - 40) / 255);
        int green_x = xPosition + 10 + (green * (width - 40) / 255);
        int blue_x = xPosition + 10 + (blue * (width - 40) / 255);
        int red_y = yPosition + height - 60;
        int green_y = yPosition + height - 40;
        int blue_y = yPosition + height - 20;
        GL11.glColor4d(1, 0, 0, 1);
        GuiHelper.drawRect(red_x, red_y, 20, 10);
        GL11.glColor4d(0, 1, 0, 1);
        GuiHelper.drawRect(green_x, green_y, 20, 10);
        GL11.glColor4d(0, 0, 1, 1);
        GuiHelper.drawRect( blue_x, blue_y, 20, 10);



        // Handle adjusting grips
        boolean mouseDown = Mouse.isButtonDown(0);

        if (mouseDown) {
            if (mouse_x >= xPosition && mouse_x <= xPosition + width - 20) {
                if (((mouse_y >= red_y && mouse_y <= red_y + 10) && grippedColor == -1) || grippedColor == 0) {
                    int value = (int) (((double) (mouse_x - (xPosition + 20)) / (double) (width - 40) * 255.0));
                    red = value > 255 ? 255 : value < 0 ? 0 : value;
                    grippedColor = 0;
                }
                if (((mouse_y >= green_y && mouse_y <= green_y + 10) && grippedColor == -1) || grippedColor == 1) {
                    int value = (int) (((double) (mouse_x - (xPosition + 20)) / (double) (width - 40) * 255.0));
                    green = value > 255 ? 255 : value < 0 ? 0 : value;
                    grippedColor = 1;
                }
                if (((mouse_y >= blue_y && mouse_y <= blue_y + 10) && grippedColor == -1) || grippedColor == 2) {
                    int value = (int) (((double) (mouse_x - (xPosition + 20)) / (double) (width - 40) * 255.0));
                    blue = value > 255 ? 255 : value < 0 ? 0 : value;
                    grippedColor = 2;
                }
            }
        } else {
            grippedColor = -1;
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public int getColor() {
        return (red << 16 | green << 8 | blue);
    }

}
