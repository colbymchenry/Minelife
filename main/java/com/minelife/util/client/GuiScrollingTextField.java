package com.minelife.util.client;

import org.lwjgl.opengl.GL11;

public class GuiScrollingTextField extends GuiScrollableContent {

    private GuiTextField textField;

    public GuiScrollingTextField(int x, int y, int width, int height)
    {
        super(x, y, width, height);
        textField = new GuiTextField(x, y, width - 5, height) {
            @Override
            public void drawBackground()
            {
//                super.drawBackground();
            }
        };
        textField.ignoreHeight = true;
    }

    @Override
    public int getObjectHeight(int index)
    {
        int totalHeight = mc.fontRenderer.FONT_HEIGHT * (mc.fontRenderer.listFormattedStringToWidth(textField.getText(), textField.getBounds().getWidth()).size());
        if (totalHeight >= bounds.getHeight()) {
            return bounds.getHeight() + (totalHeight - bounds.getHeight());
        } else {
            return bounds.getHeight();
        }
    }

    @Override
    public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(-bounds.getX(), -bounds.getY(), 0);
        textField.drawTextBox();
        GL11.glPopMatrix();
        textField.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth() - 5, getObjectHeight(0));
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
        drawRect(bounds.getX() - 2, bounds.getY() - 2, bounds.getX() + bounds.getWidth() + 2, bounds.getY() + bounds.getHeight() + 2, -6250336);
        drawRect(bounds.getX() - 1, bounds.getY() - 1, bounds.getX() + bounds.getWidth() + 1, bounds.getY() + bounds.getHeight() + 1, -16777216);
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
        textField.setFocused(textField.getBounds().contains(mouseX, mouseY));
        if (mouseY >= bounds.getX() && mouseY <= bounds.getY() + bounds.getHeight() && mouseX >= textField.getBounds().getX() &&
                mouseX <= textField.getBounds().getX() + textField.getBounds().getWidth()) {
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