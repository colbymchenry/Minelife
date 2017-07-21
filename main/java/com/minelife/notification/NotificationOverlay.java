package com.minelife.notification;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

public class NotificationOverlay {

    static final Set<AbstractGuiNotification> NOTIFICATION_QUE = new TreeSet<>();
    private static Date nextQueTime;
    private static AbstractGuiNotification currentNotification;
    private static double timeOfLastFrame = System.nanoTime() / 1e9;
    private static int notificationY;

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event)
    {
        if (event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS) return;

        // get the next notification
        currentNotification = currentNotification == null ? getNextNotification() : currentNotification;

        // if we don't have a notification to draw return
        if (currentNotification == null) return;

        // new notification is appearing, so set it up
        notificationY = -1 * currentNotification.getHeight();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5);
        nextQueTime = calendar.getTime();

        int notificationX = event.resolution.getScaledWidth() - currentNotification.getWidth();
        int speedY = 200;

        double time = System.nanoTime() / 1e9;
        double timePassed = time - timeOfLastFrame;
        timeOfLastFrame = time;

        if (Calendar.getInstance().getTime().after(nextQueTime))
            // subtract if current time is past next que time
            // that way we animate back out of the screen
            notificationY -= timePassed * speedY;
        else
            // animate the notification down
            notificationY += timePassed * speedY;

        // stop the notification from going all the way down and off the screen
        notificationY = notificationY >= 0 ? 0 : notificationY;

        GL11.glPushMatrix();
        GL11.glTranslatef(notificationX, notificationY, 400);
        currentNotification.drawNotification();
        GL11.glPopMatrix();

        // notification has animated off the screen, so delete it and start the next one
        if (notificationY <= currentNotification.getHeight() * -1) {
            NOTIFICATION_QUE.remove(currentNotification);
            currentNotification = null;
        }
    }

    private static AbstractGuiNotification getNextNotification()
    {
        return !NOTIFICATION_QUE.isEmpty() ? (AbstractGuiNotification) NOTIFICATION_QUE.toArray()[0] : null;
    }

}
