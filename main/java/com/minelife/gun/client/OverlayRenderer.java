package com.minelife.gun.client;

import com.minelife.gun.BaseGun;
import com.minelife.gun.GunRegistry;
import com.minelife.gun.gun.GunAWP;
import com.minelife.gun.gun.GunBarrett;
import com.minelife.gun.gun.client.GunClientAWP;
import com.minelife.gun.gun.client.GunClientBarrett;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

public class OverlayRenderer {

    @SubscribeEvent
    public void onGameOverlay(RenderGameOverlayEvent.Pre event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        int x = scaledResolution.getScaledWidth() / 2;
        int y = scaledResolution.getScaledHeight() / 2;

        BaseGun gun = player.getHeldItem() != null && player.getHeldItem().getItem() instanceof BaseGun ? (BaseGun) player.getHeldItem().getItem() : null;

        if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {

            if (gun != null) {
                mc.fontRenderer.drawString(BaseGun.getCurrentClipHoldings(player.getHeldItem()) + "/" +
                                ((BaseGun) player.getHeldItem().getItem()).getClipSize(),
                        x + 40, y + 40, 0xFFFFFFFF);

                /**
                 * START: Handle zooming for snipers
                 */
                if (gun == GunRegistry.get(GunBarrett.class)) {
                    if (((GunClientBarrett) gun.getClientHandler()).isZoom()) {
                        drawHollowCircle(x, y, 100);
                        event.setCanceled(true);
                    }
                }

                if (gun == GunRegistry.get(GunAWP.class)) {
                    if (((GunClientAWP) gun.getClientHandler()).isZoom()) {
                        drawHollowCircle(x, y, 100);
                        event.setCanceled(true);
                    }
                }

                /**
                 * END: Handle zooming for snipers
                 */
            }
        }

        if(event.type == RenderGameOverlayEvent.ElementType.HOTBAR || event.type == RenderGameOverlayEvent.ElementType.FOOD ||
                event.type == RenderGameOverlayEvent.ElementType.HEALTH || event.type == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            if (gun == GunRegistry.get(GunBarrett.class)) {
                if (((GunClientBarrett) gun.getClientHandler()).isZoom()) {
                    event.setCanceled(true);
                }
            }

            if (gun == GunRegistry.get(GunAWP.class)) {
                if (((GunClientAWP) gun.getClientHandler()).isZoom()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    void drawHollowCircle(float x, float y, float radius) {
        int i;
        int lineAmount = 500;
        double twicePi = 2.0f * Math.PI;

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(0, 0, 0, 1);

        GL11.glLineWidth(10);

        /**
         * Draw the circle
         */
        GL11.glBegin(GL11.GL_LINE_LOOP);
        {

            for (i = 0; i <= lineAmount; i++) {
                float edgeX = x + (radius * (float) Math.cos(i * twicePi / lineAmount));
                float edgeY = y + (radius * (float) Math.sin(i * twicePi / lineAmount));

                GL11.glVertex2f(edgeX, edgeY);

            }
        }
        GL11.glEnd();

        /**
         * Fill corners around the circle
         */
        for (i = 0; i <= lineAmount; i++) {
            GL11.glBegin(GL11.GL_LINE_LOOP);
            {
                float edgeX = x + (radius * (float) Math.cos(i * twicePi / lineAmount));
                float edgeY = y + (radius * (float) Math.sin(i * twicePi / lineAmount));

                if (edgeX < x)
                    GL11.glVertex2f(x - radius, edgeY);

                if (edgeX > x)
                    GL11.glVertex2f(x + radius, edgeY);

                GL11.glVertex2f(edgeX, edgeY);
            }
            GL11.glEnd();
        }

        /**
         * START: Draw the rectangles around the circle
         */

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        float startX = 0, startY = 0;
        float width = x - radius;
        float height = scaledResolution.getScaledHeight();
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(startX, startY + height);
            GL11.glVertex2f(startX + width, startY + height);
            GL11.glVertex2f(startX + width, startY);
            GL11.glVertex2f(startX, startY);
        }
        GL11.glEnd();

        startX = width;
        startY = 0;
        width = scaledResolution.getScaledWidth();
        height = y - radius;
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(startX, startY + height);
            GL11.glVertex2f(startX + width, startY + height);
            GL11.glVertex2f(startX + width, startY);
            GL11.glVertex2f(startX, startY);
        }
        GL11.glEnd();

        startX = 0;
        startY = y + radius;
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(startX, startY + height);
            GL11.glVertex2f(startX + width, startY + height);
            GL11.glVertex2f(startX + width, startY);
            GL11.glVertex2f(startX, startY);
        }
        GL11.glEnd();

        startX = x + radius;
        startY = 0;
        height = scaledResolution.getScaledHeight();
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(startX, startY + height);
            GL11.glVertex2f(startX + width, startY + height);
            GL11.glVertex2f(startX + width, startY);
            GL11.glVertex2f(startX, startY);
        }
        GL11.glEnd();

        /**
         * END: Drawing rectangles around the circle
         */

        GL11.glLineWidth(4);

        /**
         * Draw crosshairs
         */
        GL11.glBegin(GL11.GL_LINES);
        {
            GL11.glVertex2f(x, y - radius);
            GL11.glVertex2f(x, y + radius);
        }
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        {
            GL11.glVertex2f(x - radius, y);
            GL11.glVertex2f(x + radius, y);
        }
        GL11.glEnd();

        GL11.glLineWidth(1);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
    }

}
