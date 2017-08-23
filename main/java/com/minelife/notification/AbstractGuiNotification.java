package com.minelife.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractGuiNotification extends Gui implements Comparable<AbstractGuiNotification> {

    protected static final Set<AbstractGuiNotification> notifications = new TreeSet<>();

    private static final ResourceLocation bgTexture = new ResourceLocation("minecraft", "textures/gui/achievement/achievement_background.png");

    private static final Rectangle texTopCoords = new Rectangle(96, 202, 160, 4);
    private static final Rectangle texMiddleCoords = new Rectangle(96, 212, 160, 9);
    private static final Rectangle texBottomCoords = new Rectangle(96, 230, 160, 4);

    protected FontRenderer fontRenderer;
    protected Minecraft mc;

    protected AbstractNotification notification;

    public AbstractGuiNotification() {}

    public AbstractGuiNotification(AbstractNotification notification)
    {
        this.notification = notification;
        this.mc = Minecraft.getMinecraft();
        this.fontRenderer = mc.fontRenderer;
        notifications.add(this);
    }

    public void drawNotification()
    {
        drawBackground();
        drawForeground();
    }

    public String getSound() { return null; }

    protected abstract void drawForeground();

    protected abstract void onClick(int mouseX, int mouseY);

    protected abstract int getHeight();

    protected final int getWidth()
    {
        return texTopCoords.getWidth();
    }

    public final void push()
    {
        NotificationOverlay.NOTIFICATION_QUE.add(this);
    }

    protected void drawBackground()
    {
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_BLEND);
        Minecraft.getMinecraft().getTextureManager().bindTexture(bgTexture);

        int middleY = 0;

        // draw top curve
        drawTexturedModalRect(0, middleY, texTopCoords.getX(), texTopCoords.getY(), texTopCoords.getWidth(), texTopCoords.getHeight());
        middleY += texTopCoords.getHeight();

        // draw middle sections
        int sections = getHeight() / texMiddleCoords.getHeight();


        for (int section = 0; section < sections; section++) {
            drawTexturedModalRect(0, middleY, texMiddleCoords.getX(), texMiddleCoords.getY(), texMiddleCoords.getWidth(), texMiddleCoords.getHeight());
            middleY += texMiddleCoords.getHeight();
        }

        // if the middle sections don't fit perfectly we draw the last middle section moved up some
        if (getHeight() % texMiddleCoords.getHeight() != 0) {
            // get the height of the middle section if we added one more
            // middle section piece (it would be overlapping)
            int totalHeight = (sections + 1) * texMiddleCoords.getHeight();
            // subtract the difference in the total height of the middle section
            // by the actual height, to get the needed adjustment, and translate up
            middleY -= totalHeight - getHeight();
            // draw the last section, also add a section back to the middle coords for the bottom curve pos
            drawTexturedModalRect(0, middleY, texMiddleCoords.getX(), texMiddleCoords.getY(), texMiddleCoords.getWidth(), texMiddleCoords.getHeight());
            middleY += texMiddleCoords.getHeight();
        }

        // draw bottom curve
        drawTexturedModalRect(0, middleY, texBottomCoords.getX(), texBottomCoords.getY(), texBottomCoords.getWidth(), texBottomCoords.getHeight());
    }

    @Override
    public int compareTo(AbstractGuiNotification o)
    {
        return o.notification.getUniqueID().equals(notification.getUniqueID()) ? 0 : 1;
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof AbstractGuiNotification) && ((AbstractGuiNotification) obj).notification.getUniqueID().equals(notification.getUniqueID());
    }

    public static final AbstractGuiNotification getNotification(int index)
    {
        return notifications.toArray().length > index ? (AbstractGuiNotification) notifications.toArray()[index] : null;
    }
}
