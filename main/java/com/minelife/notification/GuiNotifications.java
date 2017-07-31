package com.minelife.notification;

import com.google.common.collect.Lists;
import com.minelife.util.client.GuiRemoveBtn;
import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.List;

public class GuiNotifications extends GuiScreen {

    private int xPosition, yPosition, bgWidth = 190, bgHeight = 200;
    private Content content;

    @Override
    public void drawScreen(int mouseX, int mouseY, float f)
    {
        super.drawScreen(mouseX, mouseY, f);
        this.drawBackground();
        this.content.draw(mouseX, mouseY, Mouse.getDWheel());
    }

    @Override
    protected void keyTyped(char key, int num)
    {
        super.keyTyped(key, num);
        this.content.keyTyped(key, num);
    }

    @Override
    public void initGui()
    {
        this.xPosition = (this.width - bgWidth) / 2;
        this.yPosition = (this.height - bgHeight) / 2;
        this.content = new Content(xPosition, yPosition, bgWidth, bgHeight);
    }

    private void drawBackground()
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(20f / 255f, 20f / 255f, 20f / 255f, 220f / 255f);
        this.drawTexturedModalRect(xPosition, yPosition, 0, 0, bgWidth, bgHeight);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static class Content extends GuiScrollableContent {

        private List<GuiRemoveBtn> removeBtnList = Lists.newArrayList();

        private Content(int xPosition, int yPosition, int width, int height)
        {
            super(xPosition, yPosition, width, height);
            AbstractGuiNotification.notifications.forEach(notification -> removeBtnList.add(new GuiRemoveBtn(width - 25, 2)));
        }

        @Override
        public int getObjectHeight(int index)
        {
            AbstractGuiNotification notification = AbstractGuiNotification.getNotification(index);
            return notification != null ? (notification.getHeight() + 8) : 0;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            AbstractGuiNotification notification = AbstractGuiNotification.getNotification(index);
            if (notification == null) return;
            notification.drawNotification();
            removeBtnList.forEach(btn -> btn.drawButton(mc, mouseX, mouseY));
        }

        @Override
        public int getSize()
        {
            return AbstractGuiNotification.notifications.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            AbstractGuiNotification notification = AbstractGuiNotification.getNotification(index);
            if (notification == null) return;
            notification.onClick(mouseX, mouseY);

            if (removeBtnList.get(index).mousePressed(mc, mouseX, mouseY)) {
                AbstractGuiNotification.notifications.removeIf(abstractGuiNotification -> abstractGuiNotification.notification.uniqueID.equals(notification.notification.uniqueID));
            }
        }

        @Override
        public void drawSelectionBox(int index, int width, int height)
        {

        }

        @Override
        public void drawBackground()
        {
        }
    }

}
