package com.minelife.essentials.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

public class OnScreenRenderer {

    public static String title;
    public static String subTitle;
    public static long endTime;

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        if(event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            if(System.currentTimeMillis() <= endTime) {
                FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
                int centerX = event.resolution.getScaledWidth() / 2;
                int centerY = event.resolution.getScaledHeight() / 2;

                if(title != null) {
                    GL11.glPushMatrix();
                    {
                        int stringWidth = fontRenderer.getStringWidth(title);

                        GL11.glTranslatef((event.resolution.getScaledWidth() - stringWidth) / 2,
                                (subTitle != null ? -30 : 0) + ((event.resolution.getScaledHeight() - fontRenderer.FONT_HEIGHT) / 2), 400);

                        GL11.glTranslatef(stringWidth / 2, fontRenderer.FONT_HEIGHT / 2, 0);
                        GL11.glScalef(4, 4, 4);
                        GL11.glTranslatef(-(stringWidth / 2), -(fontRenderer.FONT_HEIGHT / 2), 0);
                        fontRenderer.drawStringWithShadow(title, 0, 0, 0xFFFFFF);
                    }
                    GL11.glPopMatrix();
                }

                if(subTitle !=  null) {
                    GL11.glPushMatrix();
                    {
                        int stringWidth = fontRenderer.getStringWidth(subTitle);

                        GL11.glTranslatef((event.resolution.getScaledWidth() - stringWidth) / 2,
                                (title != null ? +20 : 0) + ((event.resolution.getScaledHeight() - fontRenderer.FONT_HEIGHT) / 2), 400);

                        GL11.glTranslatef(stringWidth / 2, fontRenderer.FONT_HEIGHT / 2, 0);
                        GL11.glScalef(1.5f, 1.5f, 1.5f);
                        GL11.glTranslatef(-(stringWidth / 2), -(fontRenderer.FONT_HEIGHT / 2), 0);
                        fontRenderer.drawStringWithShadow(subTitle, 0, 0, 0xFFFFFF);
                    }
                    GL11.glPopMatrix();
                }
            }
        }
    }

}
