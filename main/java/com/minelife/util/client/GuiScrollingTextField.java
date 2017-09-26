package com.minelife.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

public class GuiScrollingTextField extends GuiScrollableContent {

    private GuiTextField textField;

    public GuiScrollingTextField(Minecraft mc, FontRenderer fontRenderer, int x, int y, int width, int height)
    {
        super(mc, x, y, width, height);
        textField = new GuiTextField(fontRenderer, x, y, width, height) {
            @Override
            public void drawBackground()
            {
            }
        };
        textField.ignoreHeight = true;
    }

    @Override
    public int getObjectHeight(int index)
    {
        int totalHeight = mc.fontRenderer.FONT_HEIGHT * (mc.fontRenderer.listFormattedStringToWidth(textField.text, textField.width).size());
        if (totalHeight >= height) {
            return height + (totalHeight - height);
        } else {
            return height;
        }
    }

    @Override
    public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(-xPosition, -yPosition, 0);
        textField.drawTextBox();
        GL11.glPopMatrix();
        textField.xPosition = xPosition;
        textField.yPosition = yPosition;
        textField.width = width;
        textField.height = getObjectHeight(0);
    }

    @Override
    public int getSize()
    {
        return 1;
    }

    @Override
    public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
    {
    }

    @Override
    public void drawSelectionBox(int index, int width, int height)
    {
    }

    @Override
    public void drawBackground()
    {
        drawRect(xPosition - 2, yPosition - 2, xPosition + width + 2, yPosition + height + 2, -6250336);
        drawRect(xPosition - 1, yPosition - 1, xPosition + width + 1, yPosition + height + 1, -16777216);
    }
    @Override
    public void keyTyped(char keycode, int keynum)
    {
        super.keyTyped(keycode, keynum);
        textField.keyTyped(keycode, keynum);
        // move the window view to wherever the cursor is
        int y = textField.getCursorLine() * mc.fontRenderer.FONT_HEIGHT;
        setScrollY(y);
    }

    public void mouseClicked(int mouseX, int mouseY)
    {
        textField.setFocused(textField.contains(mouseX, mouseY));
        if (mouseY >= yPosition && mouseY <= yPosition + height && mouseX >= textField.xPosition &&
                mouseX <= textField.xPosition + textField.width) {
            int y = (mouseY + (int) (getScrollY()));
            textField.mouseClicked(mouseX, y);
        }
    }

    public void update()
    {
        textField.update();
    }

    public GuiTextField getTextField()
    {
        return textField;
    }
}