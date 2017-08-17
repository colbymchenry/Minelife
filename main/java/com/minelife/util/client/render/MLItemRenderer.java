package com.minelife.util.client.render;

import codechicken.lib.render.BlockRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Iterator;
import java.util.List;

public class MLItemRenderer {

    private RenderItem itemRender;
    private ItemRenderer itemRenderer;
    public FontRenderer fontRendererObj;
    private float zLevel;
    private Minecraft mc;

    public MLItemRenderer(Minecraft mc)
    {
        this.mc = mc;
        itemRenderer = new ItemRenderer(mc);
        itemRender = new RenderItem();
        fontRendererObj = mc.fontRenderer;
        this.zLevel = 0F;
    }

    public void attempt_gl_reset()
    {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1, 1, 1, 1);
    }

    public void renderItem3D(ItemStack itemStack, int x, int y, int scale, float rotY)
    {
        GL11.glPushMatrix();
        {
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glTranslatef(x, y, 400.0F);
            if(Block.getBlockFromItem(itemStack.getItem()) == Blocks.air || !RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemStack.getItem()).getRenderType())) {
                GL11.glTranslatef(0, 20, 0);
            }
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(180f, 1f, 0f, 0f);
            GL11.glRotatef(rotY, 0f, 1f, 0f);
            GL11.glRotatef(-25f, 1f, 0f, 1f);

            GL11.glDisable(GL11.GL_CULL_FACE);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            itemRenderer.renderItem(mc.thePlayer, itemStack, 0);
        }
        GL11.glPopMatrix();
    }

    public void drawItemStack(ItemStack p_146982_1_, int p_146982_2_, int p_146982_3_, String p_146982_4_)
    {
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) 240 / 1.0F, (float) 240 / 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_LIGHTING);

        this.zLevel = 200.0F;
        itemRender.zLevel = 200.0F;
        FontRenderer font = null;
        if (p_146982_1_ != null) font = p_146982_1_.getItem().getFontRenderer(p_146982_1_);
        if (font == null) font = fontRendererObj;
        itemRender.renderItemAndEffectIntoGUI(font, this.mc.getTextureManager(), p_146982_1_, p_146982_2_, p_146982_3_);
        itemRender.renderItemOverlayIntoGUI(font, this.mc.getTextureManager(), p_146982_1_, p_146982_2_, p_146982_3_, p_146982_4_);
        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
    }

    public int[] getToolTipBounds(ItemStack p_146285_1_, int p_146285_2_, int p_146285_3_, int screenWidth, int screenHeight) {
        List list = p_146285_1_.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

        for (int k = 0; k < list.size(); ++k) {
            if (k == 0) {
                list.set(k, p_146285_1_.getRarity().rarityColor + (String) list.get(k));
            } else {
                list.set(k, EnumChatFormatting.GRAY + (String) list.get(k));
            }
        }

        FontRenderer font = p_146285_1_.getItem().getFontRenderer(p_146285_1_);
        return getToolTipBounds(list, p_146285_2_, p_146285_3_, (font == null ? fontRendererObj : font), screenWidth, screenHeight);
    }

    private int[] getToolTipBounds(List p_146283_1_, int p_146283_2_, int p_146283_3_, FontRenderer font, int screenWidth, int screenHeight) {
        if (!p_146283_1_.isEmpty()) {
            int k = 0;
            Iterator iterator = p_146283_1_.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                int l = font.getStringWidth(s);

                if (l > k) {
                    k = l;
                }
            }

            int j2 = p_146283_2_ + 12;
            int k2 = p_146283_3_ - 12;
            int i1 = 8;

            if (p_146283_1_.size() > 1) {
                i1 += 2 + (p_146283_1_.size() - 1) * 10;
            }

            if (j2 + k > screenWidth) {
                j2 -= 28 + k;
            }

            if (k2 + i1 + 6 > screenHeight) {
                k2 = screenHeight - i1 - 6;
            }

            int x = j2 - 4;
            int y = k2 - 4;
            int width =  k;
            int height = i1;
            return new int[]{width, height};
        }

        return new int[]{0, 0};
    }

    public void renderToolTip(ItemStack p_146285_1_, int p_146285_2_, int p_146285_3_, int screenWidth, int screenHeight)
    {
        List list = p_146285_1_.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

        for (int k = 0; k < list.size(); ++k) {
            if (k == 0) {
                list.set(k, p_146285_1_.getRarity().rarityColor + (String) list.get(k));
            } else {
                list.set(k, EnumChatFormatting.GRAY + (String) list.get(k));
            }
        }

        FontRenderer font = p_146285_1_.getItem().getFontRenderer(p_146285_1_);
        drawHoveringText(list, p_146285_2_, p_146285_3_, (font == null ? fontRendererObj : font), screenWidth, screenHeight);
    }

    protected void drawHoveringText(List p_146283_1_, int p_146283_2_, int p_146283_3_, FontRenderer font, int screenWidth, int screenHeight)
    {
        if (!p_146283_1_.isEmpty()) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            Iterator iterator = p_146283_1_.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                int l = font.getStringWidth(s);

                if (l > k) {
                    k = l;
                }
            }

            int j2 = p_146283_2_ + 12;
            int k2 = p_146283_3_ - 12;
            int i1 = 8;

            if (p_146283_1_.size() > 1) {
                i1 += 2 + (p_146283_1_.size() - 1) * 10;
            }

            if (j2 + k > screenWidth) {
                j2 -= 28 + k;
            }

            if (k2 + i1 + 6 > screenHeight) {
                k2 = screenHeight - i1 - 6;
            }

            this.zLevel = 300.0F;
            itemRender.zLevel = 300.0F;
            int j1 = -267386864;
            this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

            for (int i2 = 0; i2 < p_146283_1_.size(); ++i2) {
                String s1 = (String) p_146283_1_.get(i2);
                font.drawStringWithShadow(s1, j2, k2, -1);

                if (i2 == 0) {
                    k2 += 2;
                }

                k2 += 10;
            }

            this.zLevel = 0.0F;
            itemRender.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

    protected void drawGradientRect(int p_73733_1_, int p_73733_2_, int p_73733_3_, int p_73733_4_, int p_73733_5_, int p_73733_6_)
    {
        float f = (float) (p_73733_5_ >> 24 & 255) / 255.0F;
        float f1 = (float) (p_73733_5_ >> 16 & 255) / 255.0F;
        float f2 = (float) (p_73733_5_ >> 8 & 255) / 255.0F;
        float f3 = (float) (p_73733_5_ & 255) / 255.0F;
        float f4 = (float) (p_73733_6_ >> 24 & 255) / 255.0F;
        float f5 = (float) (p_73733_6_ >> 16 & 255) / 255.0F;
        float f6 = (float) (p_73733_6_ >> 8 & 255) / 255.0F;
        float f7 = (float) (p_73733_6_ & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex((double) p_73733_3_, (double) p_73733_2_, (double) this.zLevel);
        tessellator.addVertex((double) p_73733_1_, (double) p_73733_2_, (double) this.zLevel);
        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex((double) p_73733_1_, (double) p_73733_4_, (double) this.zLevel);
        tessellator.addVertex((double) p_73733_3_, (double) p_73733_4_, (double) this.zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }


}
