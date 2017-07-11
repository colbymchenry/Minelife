package com.minelife.notification;

import com.minelife.util.client.GuiScrollList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiNotifications extends GuiScreen {

    private int xPosition, yPosition, bgWidth = 200, bgHeight = 200;
    private Content content;

    private GuiButton deleteBtn;

    @Override
    public void drawScreen(int mouseX, int mouseY, float f)
    {
        super.drawScreen(mouseX, mouseY, f);
        this.drawBackground();
        this.content.draw(mouseX, mouseY, Mouse.getDWheel());
        this.deleteBtn.drawButton(mc, mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char key, int num)
    {
        super.keyTyped(key, num);
        this.content.keyTyped(key, num);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn)
    {
        super.mouseClicked(mouseX, mouseY, mouseBtn);
        if (this.deleteBtn.mousePressed(mc, mouseX, mouseY)) {
            AbstractNotification notification = AbstractNotification.getNotification(content.getSelected());
            if (notification != null) AbstractNotification.notifications.remove(notification);
        }
    }

    @Override
    public void initGui()
    {
        this.xPosition = (this.width - bgWidth) / 2;
        this.yPosition = (this.height - bgHeight) / 2;
        this.content = new Content(xPosition, yPosition, bgWidth, bgHeight);
        this.deleteBtn = new GuiButton(0, xPosition + ((bgWidth - 75) / 2), yPosition + bgHeight + 10, 75, 20, "Delete");
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

    public static class Content extends GuiScrollList {

        private Content(int xPosition, int yPosition, int width, int height)
        {
            super(xPosition, yPosition, width, height);
        }

        @Override
        public int getObjectHeight(int index)
        {
            AbstractNotification notification = AbstractNotification.getNotification(index);
            return notification != null ? notification.getHeight() : 0;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            AbstractNotification notification = AbstractNotification.getNotification(index);
            if (notification == null) return;
            notification.drawNotification();
        }

        @Override
        public int getSize()
        {
            return AbstractNotification.notifications.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            if (doubleClick) {
                AbstractNotification notification = AbstractNotification.getNotification(index);
                if (notification == null) return;
                notification.onClick(mouseX, mouseY);
                AbstractNotification.notifications.remove(notification);
            }
        }

        @Override
        public void drawBackground()
        {
        }
    }

}
