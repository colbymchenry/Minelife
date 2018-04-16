package com.minelife.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiScrollableContent extends Gui {

    public int x, y, width, height;

    private float scrollY = 0, initialMouseClickY = -1f;
    public int selected = -1;
    private long lastClickTime = 0L;
    public boolean drawScrollbarOnTop = true;
    protected Minecraft mc;
    private boolean mouseDown = false;

    public GuiScrollableContent(Minecraft mc, int x, int y, int width, int height) {
        this.mc = mc;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getGripWidth() {
        return 5;
    }

    public float getSingleUnit() {
        int contentHeight = getContentHeight();
        contentHeight = contentHeight == 0 ? 1 : contentHeight;
        return 10 * ((float) contentHeight / (float) height);
    }

    public boolean contains(int x, int y) {
        return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
    }

    public int getMaximumGripSize() {
        return height;
    }

    public int getMinimumGripSize() {
        return 20;
    }

    // have to ask for dWheel to prevent scrolling not working when two scroll lists are in use.
    public void draw(int mouseX, int mouseY, int dWheel) {
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

        float scrollBarLeft = x + width - getGripWidth();
        float scrollBarRight = x + width;

        if (Mouse.isButtonDown(0)) {
            if (this.initialMouseClickY == -1.0F) {
                if (isHovering) {

                    if (!mouseDown) {
                        if (mouseX >= scrollBarLeft && mouseX <= scrollBarRight && (mouseY - y) < gripPosition) {
                            scrollY -= getSingleUnit();
                        } else if (mouseX >= scrollBarLeft && mouseX <= scrollBarRight && (mouseY - y) > gripPosition + gripSize) {
                            scrollY += getSingleUnit();
                        } else {
                            int totalHeight = 0;
                            for (int i = 0; i < getSize(); i++) {
                                if (mouseY >= y + totalHeight - scrollY && mouseY <= (y + totalHeight - scrollY) + getObjectHeight(i) && mouseX >= x && mouseX <= x + width - getGripWidth()) {

                                    int mX = (mouseX - x);
                                    int mY = mouseY - ((int) ((y + totalHeight) - scrollY));

                                    this.elementClicked(i, mX, mY, i == this.selected && System.currentTimeMillis() - this.lastClickTime < 250L);
                                    this.selected = i;
                                    this.lastClickTime = System.currentTimeMillis();
                                }
                                totalHeight += getObjectHeight(i);
                            }

                            if (mouseX >= scrollBarLeft && mouseX <= scrollBarRight)
                                this.initialMouseClickY = Math.abs((mouseY - y) - gripPosition);
                        }

                        mouseDown = true;
                    }

                }
            } else {
                boolean above = ((mouseY - y) - gripPosition) < 0;
                float newGripPosition = (mouseY - y) + (above ? 0 : -initialMouseClickY);
                newGripPosition = newGripPosition < 0 ? 0 : newGripPosition > trackScrollAreaSize ? trackScrollAreaSize : newGripPosition;
                float newGripPositionRatio = newGripPosition / trackScrollAreaSize;
                scrollY = newGripPositionRatio * windowScrollAreaSize;

                gripPosition = newGripPosition;
            }
        } else {
            mouseDown = false;

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

        GlStateManager.disableTexture2D();
        drawBackground();
        GlStateManager.enableTexture2D();

        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        double scaleW = Minecraft.getMinecraft().displayWidth / res.getScaledWidth_double();
        double scaleH = Minecraft.getMinecraft().displayHeight / res.getScaledHeight_double();

        int left = x;
        int bottom = y + height;
        int listWidth = width - getGripWidth();
        int viewHeight = height;

        boolean drawScrollbar = gripSize != height;

        if (!drawScrollbarOnTop && drawScrollbar) {
            drawTrack();
            GlStateManager.disableTexture2D();
            GlStateManager.pushMatrix();
            GlStateManager.translate(scrollBarLeft, y + gripPosition, 0);
            drawGrip(MathHelper.floor(gripSize));
            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();
        }

        // use scissoring to make the drawing seem seamless and continuous
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) (left * scaleW), (int) (mc.displayHeight - (bottom * scaleH)),
                (int) (listWidth * scaleW), (int) (viewHeight * scaleH));

        // drawTickBox all of the content object by object
        int totalHeight = 0;
        for (int i = 0; i < getSize(); i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, (y + totalHeight) - scrollY, 0);

            if (selected == i) {
                drawSelectionBox(i, width, getObjectHeight(i));
            }

            int mX = (mouseX - x);
            int mY = mouseY - ((int) ((y + totalHeight) - scrollY));
            boolean hovering = mX > 0 && mX < listWidth && mY > 0 && mY < getObjectHeight(i);

            drawObject(i, mX, mY, hovering);
            GlStateManager.popMatrix();
            totalHeight += getObjectHeight(i);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if (drawScrollbarOnTop && drawScrollbar) {
            drawTrack();
            GlStateManager.disableTexture2D();
            GlStateManager.pushMatrix();
            GlStateManager.translate(scrollBarLeft, y + gripPosition, 0);
            drawGrip(MathHelper.floor(gripSize));
            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();
        }
    }

    public void keyTyped(char keycode, int keynum) {
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

    public int getContentHeight() {
        int contentHeight = 0;

        for (int i = 0; i < getSize(); i++)
            contentHeight += getObjectHeight(i);

        return contentHeight;
    }

    public void drawBackground() {
        GlStateManager.disableTexture2D();
        GlStateManager.color(0f, 0f, 0f, 1f);
        GuiHelper.drawRect(x, y, width, height);
        GlStateManager.enableTexture2D();
        GlStateManager.color(1, 1, 1, 1);
    }

    public void drawGrip(int gripHeight) {
        GlStateManager.disableTexture2D();
        GL11.glColor4f(90f / 255f, 90f / 255f, 90f / 255f, 1f);
        GuiHelper.drawRect(0, 0, getGripWidth(), gripHeight);
        GlStateManager.enableTexture2D();
        GlStateManager.color(1, 1, 1, 1);
    }

    public void drawSelectionBox(int index, int width, int height) {
        GlStateManager.disableTexture2D();
        GlStateManager.color(128f / 255f, 128f / 255f, 128f / 255f, 1f);
        GuiHelper.drawRect(0, 0, width, height);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GlStateManager.enableTexture2D();
    }

    public void drawTrack() {
        GlStateManager.disableTexture2D();
        GlStateManager.color(60f / 255f, 60f / 255f, 60f / 255f, 1f);
        GuiHelper.drawRect(x + width - getGripWidth(), y, getGripWidth(), height);
        GlStateManager.enableTexture2D();
        GL11.glColor4f(1, 1, 1, 1);
    }

    public abstract int getObjectHeight(int index);

    public abstract void drawObject(int index, int mouseX, int mouseY, boolean isHovering);

    public abstract int getSize();

    public abstract void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick);

    public float getScrollY() {
        return scrollY;
    }

    public void setScrollY(float scrollY) {
        float windowScrollAreaSize = getMaxScrollY();
        this.scrollY = scrollY < 0 ? 0 : scrollY > windowScrollAreaSize ? windowScrollAreaSize : scrollY;
    }

    public float getMaxScrollY() {
        return getContentHeight() - height;
    }
}
