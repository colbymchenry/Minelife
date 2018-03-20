package com.minelife.essentials.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

public class OnScreenRenderer {

    public static String title;
    public static String subTitle;
    public static long endTime;

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            if (System.currentTimeMillis() <= endTime) {
                FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
                int centerX = event.getResolution().getScaledWidth() / 2;
                int centerY = (event.getResolution().getScaledHeight() - fontRenderer.FONT_HEIGHT) / 2;

                if (title != null) {
                    GlStateManager.pushMatrix();
                    GlStateManager.color(1, 1, 1, 1);
                    int stringWidth = fontRenderer.getStringWidth(title);

                    GlStateManager.translate(centerX, (subTitle != null ? -30 : 0) + centerY, 400);

                    GlStateManager.translate(stringWidth / 2, fontRenderer.FONT_HEIGHT / 2, 0);
                    GlStateManager.scale(4, 4, 4);
                    GlStateManager.translate(-(stringWidth / 2), -(fontRenderer.FONT_HEIGHT / 2), 0);
                    fontRenderer.drawStringWithShadow(title, 0, 0, 0xFFFFFF);
                    GlStateManager.popMatrix();
                }

                if (subTitle != null) {
                    GlStateManager.pushMatrix();
                    GlStateManager.color(1, 1, 1, 1);
                    int stringWidth = fontRenderer.getStringWidth(subTitle);

                    GlStateManager.translate(centerX, (title != null ? +20 : 0) + centerY, 400);

                    GlStateManager.translate(stringWidth / 2, fontRenderer.FONT_HEIGHT / 2, 0);
                    GlStateManager.scale(1.5f, 1.5f, 1.5f);
                    GlStateManager.translate(-(stringWidth / 2), -(fontRenderer.FONT_HEIGHT / 2), 0);
                    fontRenderer.drawStringWithShadow(subTitle, 0, 0, 0xFFFFFF);
                    GlStateManager.popMatrix();
                }
            }
        }
    }

}
