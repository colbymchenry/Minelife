package com.minelife.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiScrollableContent extends Gui {

    public int xPosition, yPosition, width, height;

    private float scrollY = 0, initialMouseClickY = -1f;
    public int selected = -1;
    private long lastClickTime = 0L;
    public boolean drawScrollbarOnTop = true;
    protected Minecraft mc;

    public GuiScrollableContent(Minecraft mc, int xPosition, int yPosition, int width, int height)
    {
        this.mc = mc;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
    }

    public int getGripWidth()
    {
        return 5;
    }

    public float getSingleUnit()
    {
        int contentHeight = getContentHeight();
        contentHeight = contentHeight == 0 ? 1 : contentHeight;
        return 10 * ((float) contentHeight / (float) height);
    }

    public boolean contains(int x, int y)
    {
        return x >= xPosition && x <= xPosition + width && y >= yPosition && y <= yPosition + height;
    }

    public int getMaximumGripSize()
    {
        return height;
    }

    public int getMinimumGripSize()
    {
        return 20;
    }

    // have to ask for dWheel to prevent scrolling not working when two scroll lists are in use.
    public void draw(int mouseX, int mouseY, int dWheel)
    {
        int contentHeight = getContentHeight();
        contentHeight = contentHeight == 0 ? 1 : contentHeight;

        float ratio = (float) height / (float) contentHeight;
        float gripSize = height * ratio;
        float trackSize = height;

        // keep the gripSize reasonable.
        gripSize = gripSize > getMaximumGripSize() ? getMaximumGripSize() : gripSize < getMinimumGripSize() ? getMinimumGripSize() : gripSize;

        float trackScrollAreaSize = trackSize - gripSize;
        float windowScrollAreaSize = getMaxScrollY();
        float windowPositionRatio = scrollY / windowScrollAreaSize;
        float gripPosition = trackScrollAreaSize * windowPositionRatio;

        boolean isHovering = contains(mouseX, mouseY);

        float scrollBarLeft = xPosition + width - getGripWidth();
        float scrollBarRight = xPosition + width;

        if (Mouse.isButtonDown(0)) {
            if (this.initialMouseClickY == -1.0F) {
                if (isHovering) {

                    if (mouseX >= scrollBarLeft && mouseX <= scrollBarRight && (mouseY - yPosition) < gripPosition) {
                        scrollY -= getSingleUnit();
                    } else if (mouseX >= scrollBarLeft && mouseX <= scrollBarRight && (mouseY - yPosition) > gripPosition + gripSize) {
                        scrollY += getSingleUnit();

                    } else {
                        int totalHeight = 0;
                        for (int i = 0; i < getSize(); i++) {
                            if (mouseY >= yPosition + totalHeight - scrollY && mouseY <= (yPosition + totalHeight - scrollY) + getObjectHeight(i) && mouseX >= xPosition && mouseX <= xPosition + width - getGripWidth()) {

                                int mX = (mouseX - xPosition);
                                int mY = mouseY - ((int) ((yPosition + totalHeight) - scrollY));

                                this.elementClicked(i, mX, mY, i == this.selected && System.currentTimeMillis() - this.lastClickTime < 250L);
                                this.selected = i;
                                this.lastClickTime = System.currentTimeMillis();
                            }
                            totalHeight += getObjectHeight(i);
                        }
                        this.initialMouseClickY = Math.abs((mouseY - yPosition) - gripPosition);
                    }

                }
            } else {
                boolean above = ((mouseY - yPosition) - gripPosition) < 0;
                float newGripPosition = (mouseY - yPosition) + (above ? 0 : -initialMouseClickY);
                newGripPosition = newGripPosition < 0 ? 0 : newGripPosition > trackScrollAreaSize ? trackScrollAreaSize : newGripPosition;
                float newGripPositionRatio = newGripPosition / trackScrollAreaSize;
                scrollY = newGripPositionRatio * windowScrollAreaSize;

                gripPosition = newGripPosition;
            }
        } else {
            int scroll = dWheel;
            if (isHovering && scroll != 0) {
                if (scroll > 0) scroll = -1;
                else if (scroll < 0) scroll = 1;
                this.scrollY += scroll * getSingleUnit() / 2;
            }

            initialMouseClickY = -1f;
        }


        // just some fixes to keep the window from going out of view
        if (scrollY > windowScrollAreaSize) scrollY = windowScrollAreaSize;
        if (scrollY < 0) scrollY = 0;
        if (Float.isNaN(scrollY)) scrollY = 0;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        drawBackground();
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        double scaleW = Minecraft.getMinecraft().displayWidth / res.getScaledWidth_double();
        double scaleH = Minecraft.getMinecraft().displayHeight / res.getScaledHeight_double();

        int left = xPosition;
        int bottom = yPosition + height;
        int listWidth = width - getGripWidth();
        int viewHeight = height;

        boolean drawScrollbar = gripSize != height;

        if (!drawScrollbarOnTop && drawScrollbar) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glPushMatrix();
            GL11.glTranslatef(scrollBarLeft, yPosition + gripPosition, 0);
            drawGrip(gripSize);
            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        // use scissoring to make the drawing seem seamless and continuous
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) (left * scaleW), (int) (mc.displayHeight - (bottom * scaleH)),
                (int) (listWidth * scaleW), (int) (viewHeight * scaleH));

        // drawTickBox all of the content object by object
        int totalHeight = 0;
        for (int i = 0; i < getSize(); i++) {
            GL11.glPushMatrix();
            GL11.glTranslatef(xPosition, (yPosition + totalHeight) - scrollY, 0);

            if (selected == i) {
                drawSelectionBox(i, width, getObjectHeight(i));
            }

            int mX = (mouseX - xPosition);
            int mY = mouseY - ((int) ((yPosition + totalHeight) - scrollY));
            boolean hovering = mX > 0 && mX < listWidth && mY > 0 && mY < getObjectHeight(i);

            drawObject(i, mX, mY, hovering);
            GL11.glPopMatrix();
            totalHeight += getObjectHeight(i);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        drawTrack();

        if (drawScrollbarOnTop && drawScrollbar) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glPushMatrix();
            GL11.glTranslatef(scrollBarLeft, yPosition + gripPosition, 0);
            drawGrip(gripSize);
            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

    public void keyTyped(char keycode, int keynum)
    {
        float aSingleUnit = getSingleUnit();

        if (keynum == Keyboard.KEY_UP) {
            scrollY = scrollY - aSingleUnit;
            if (scrollY < 0)
                scrollY = 0;
        }

        if (keynum == Keyboard.KEY_DOWN) {
            float windowScrollAreaSize = getContentHeight() - height;

            scrollY = scrollY + aSingleUnit;
            if (scrollY > windowScrollAreaSize)
                scrollY = windowScrollAreaSize;
        }
    }

    public int getContentHeight()
    {
        int contentHeight = 0;

        for (int i = 0; i < getSize(); i++)
            contentHeight += getObjectHeight(i);

        return contentHeight;
    }

    public void drawBackground()
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(0f, 0f, 0f, 1f);
        GuiUtil.drawImage(xPosition, yPosition, width, height);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void drawGrip(float gripHeight)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(90f / 255f, 90f / 255f, 90f / 255f, 1f);
        GuiUtil.drawImage(0, 0, getGripWidth(), gripHeight);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void drawSelectionBox(int index, int width, int height)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(128f / 255f, 128f / 255f, 128f / 255f, 1f);
        GuiUtil.drawImage(0, 0, width, height);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void drawTrack() {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(60f / 255f, 60f / 255f, 60f / 255f, 1f);
        GuiUtil.drawImage(xPosition + width - getGripWidth(), yPosition, getGripWidth(), height);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public abstract int getObjectHeight(int index);

    public abstract void drawObject(int index, int mouseX, int mouseY, boolean isHovering);

    public abstract int getSize();

    public abstract void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick);

    public float getScrollY()
    {
        return scrollY;
    }

    public void setScrollY(float scrollY)
    {
        float windowScrollAreaSize = getMaxScrollY();
        this.scrollY = scrollY < 0 ? 0 : scrollY > windowScrollAreaSize ? windowScrollAreaSize : scrollY;
    }

    public float getMaxScrollY()
    {
        return getContentHeight() - height;
    }
}
