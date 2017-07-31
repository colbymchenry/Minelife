package com.minelife.notification;

import com.minelife.Minelife;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;

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

        boolean new_notification = currentNotification == null;

        // get the next notification
        currentNotification = currentNotification == null ? getNextNotification() : currentNotification;

        if (currentNotification == null) return;

        // new notification is appearing, so set it up
        if (new_notification) {
            notificationY = -1 * currentNotification.getHeight();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, 5);
            nextQueTime = calendar.getTime();
            timeOfLastFrame = System.nanoTime() / 1e9;
            if (currentNotification.getSound() != null)
                Minecraft.getMinecraft().thePlayer.playSound(Minelife.MOD_ID + ":" + currentNotification.getSound(), 0.5F, 1.0F);
        }

        int notificationX = event.resolution.getScaledWidth() - (currentNotification.getWidth() + 1);
        double time = System.nanoTime() / 1e9;
        double timePassed = time - timeOfLastFrame;
        timeOfLastFrame = time;
        int speedY = 200;

        if (Calendar.getInstance().getTime().after(nextQueTime)) {
            // subtract if current time is past next que time
            // that way we animate back out of the screen
            notificationY -= timePassed * speedY;

            if (notificationY < (currentNotification.getHeight() + 80) * -1) {
                NOTIFICATION_QUE.removeIf(abstractGuiNotification -> abstractGuiNotification.notification.uniqueID.equals(currentNotification.notification.uniqueID));
                currentNotification = null;
                return;
            }
        } else {
            // animate the notification down
            notificationY += timePassed * speedY;
        }

        // stop the notification from going all the way down and off the screen
        notificationY = notificationY >= 1 ? 1 : notificationY;

        GL11.glPushMatrix();
        GL11.glTranslatef(notificationX, notificationY, 400);
        currentNotification.drawNotification();
        GL11.glPopMatrix();
    }

    private static AbstractGuiNotification getNextNotification()
    {
        return !NOTIFICATION_QUE.isEmpty() ? (AbstractGuiNotification) NOTIFICATION_QUE.toArray()[0] : null;
    }

}
