package com.minelife.notifications.client;

import com.minelife.notifications.Notification;
import com.minelife.notifications.NotificationClickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.Iterator;
import java.util.LinkedList;

public class OverlayRenderer {

    private static LinkedList<NotificationProperties> notificationsForRendering = new LinkedList<>();
    private static boolean mouseDown = false;

    @SubscribeEvent
    public void onGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR || (Minecraft.getMinecraft().currentScreen != null &&
                !(Minecraft.getMinecraft().currentScreen instanceof GuiChat))) return;

        if (ClientProxy.notifications.isEmpty()) return;

        Iterator<NotificationProperties> iterator = notificationsForRendering.descendingIterator();

        int posY = 0;

        GlStateManager.pushAttrib();
        while (iterator.hasNext()) {
            NotificationProperties n = iterator.next();

            GlStateManager.pushMatrix();
            {
                int posX = event.getResolution().getScaledWidth();

                n.posX = n.posX == 0 ? posX : n.posX;

                boolean viewingChat = Minecraft.getMinecraft().currentScreen instanceof GuiChat;

                if (n.done) {
                    n.update(posX, posY);
                } else {
                    if (System.currentTimeMillis() - n.startTime > n.notification.getDuration() * 1000L) {
                        if (viewingChat) {
                            n.update(posX - n.notification.getNotificationType().width, posY);
                        } else {
                            n.update(posX, posY);
                        }
                    } else {
                        n.update(posX - n.notification.getNotificationType().width, posY);
                    }
                }

                GlStateManager.translate(n.posX, n.posY, 100f);

                int mouseX = Mouse.getX() / event.getResolution().getScaleFactor();
                int mouseY = event.getResolution().getScaledHeight() - (Mouse.getY() / event.getResolution().getScaleFactor());

                int mX = mouseX - (int) n.posX;
                int mY = mouseY - (int) n.posY;

                n.notification.drawNotification();

                if (Mouse.isButtonDown(0)) {
                    if (!mouseDown) {
                        mouseDown = true;
                    }
                } else {
                    mouseDown = false;
                }

                if (viewingChat) {
                    if (mouseX > n.posX && mouseX < n.posX + n.notification.getNotificationType().width && mouseY > n.posY && mouseY < n.posY + n.notification.getHeight() && Mouse.isButtonDown(0)) {
                        Minecraft.getMinecraft().player.closeScreen();
                        NotificationClickEvent clickEvent = new NotificationClickEvent(n.notification);
                        MinecraftForge.EVENT_BUS.post(clickEvent);
                        n.done = true;
                    }
                }

                if (n.posX >= posX) iterator.remove();


                posY += n.notification.getHeight() + 1;
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.popAttrib();
    }

    public static void addNotification(Notification notification, boolean playSound, boolean render) {
        if (render) notificationsForRendering.addLast(new NotificationProperties(notification));
        ClientProxy.notifications.addLast(notification);

        if(!playSound) return;

        EntityPlayer player = Minecraft.getMinecraft().player;
        player.getEntityWorld().playSound(player, player.posX, player.posY, player.posZ,
                new SoundEvent(new ResourceLocation("minelife:text_message")), SoundCategory.MASTER, 0.2F, 1.0F);
    }

    public static class NotificationProperties {

        private long startTime;
        protected Notification notification;
        private double posX, posY, speedX = 200f, speedY = 200f;
        private boolean done = false;

        private double timeOfLastFrame = System.nanoTime() / 1e9;

        public NotificationProperties(Notification notification) {
            this.notification = notification;
            this.startTime = System.currentTimeMillis();
        }

        private void update(double neededX, double neededY) {
            double time = System.nanoTime() / 1e9;
            double timePassed = time - timeOfLastFrame;
            timeOfLastFrame = time;
            update(timePassed, neededX, neededY);
        }


        private void update(double time, double neededX, double neededY) {
            posX = posX < neededX ? posX + (time * speedX) > neededX ? neededX : posX + (time * speedX) : posX - (time * speedX) < neededX ? neededX : posX - (time * speedX);
            posY = posY < neededY ? posY + (time * speedY) > neededY ? neededY : posY + (time * speedY) : posY - (time * speedY) < neededY ? neededY : posY - (time * speedY);
        }

        public Notification getNotification() {
            return notification;
        }
    }


}
