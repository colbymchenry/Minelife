package com.minelife.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiHelper {

    private static final GuiScreen helperScreen = new GuiScreen() {
        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        }
    };

    public static void drawRect(int x, int y, int width, int height) {
//        helperScreen.drawTexturedModalRect(x, y, 0, 0, width, height);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, 0).tex(0.0, 1.0);
        buffer.pos(x + width, y + height, 0).tex(1.0, 1.0);
        buffer.pos(x + width, y, 0).tex(1.0, 0.0);
        buffer.pos(x, y, 0).tex(0.0, 0.0);
        tessellator.draw();
    }

    public static void drawDefaultBackground(int x, int y, int width, int height) {
        drawDefaultBackground(x, y, width, height, 0xC6C6C6);
    }

    public static void drawDefaultBackground(int x, int y, int width, int height, int colorRGB) {
        Color color = new Color(colorRGB);
        Color bottomBorder = color.darker().darker();
        Color topColor = color.brighter().brighter();
        Color border = color.darker().darker().darker().darker().darker().darker().darker();

        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0f);

        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        drawRect(2, 1, width - 4, 1);
        drawRect(1, 2, width - 2, 1);
        drawRect(1, 3, width - 1, height - 5);
        drawRect(2, height - 2, width - 2, 1);
        drawRect(3, height - 1, width - 4, 1);

        GlStateManager.color(border.getRed() / 255f, border.getGreen() / 255f, border.getBlue() / 255f, border.getAlpha() / 255f);
        drawRect(2, 0, width - 4, 1);
        drawRect(3, height, width - 4, 1);
        drawRect(0, 2, 1, height - 3);
        drawRect(width, 3, 1, height - 4);
        drawRect(1, 1, 1, 1);
        drawRect(1, height - 2, 1, 1);
        drawRect(2, height - 1, 1, 1);
        drawRect(width - 2, 1, 1, 1);
        drawRect(width - 1, 2, 1, 1);
        drawRect(width - 1, height - 1, 1, 1);

        GlStateManager.color(topColor.getRed() / 255f, topColor.getGreen() / 255f, topColor.getBlue() / 255f, topColor.getAlpha() / 255f);
        drawRect(2, 1, width - 4, 2);
        drawRect(1, 2, 2, height - 4);
        drawRect(3, 3, 1, 1);

        GlStateManager.color(bottomBorder.getRed() / 255f, bottomBorder.getGreen() / 255f, bottomBorder.getBlue() / 255f, bottomBorder.getAlpha() / 255f);
        drawRect(3, height - 2, width - 4, 2);
        drawRect(width - 2, 3, 2, height - 4);
        drawRect(width - 3, height - 3, 1, 1);

        GlStateManager.popMatrix();

        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        IBakedModel bakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
        bakedModel = ForgeHooksClient.handleCameraTransforms(bakedModel, transformType, false);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, bakedModel);
    }

}
