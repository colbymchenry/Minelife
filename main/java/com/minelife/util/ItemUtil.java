package com.minelife.util;

import com.minelife.util.client.GuiUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Iterator;
import java.util.List;

public class ItemUtil {

    private static RenderItem renderItem = RenderItem.getInstance();
    private static ItemRenderer itemRenderer = new ItemRenderer(Minecraft.getMinecraft());

    public static String itemToString(ItemStack itemStack)
    {
        NBTTagCompound tagCompound = itemStack.writeToNBT(new NBTTagCompound());
        return tagCompound.toString();
    }

    public static ItemStack itemFromString(String s)
    {
        return ItemStack.loadItemStackFromNBT(NBTUtil.fromString(s));
    }

    @SideOnly(Side.CLIENT)
    public static class Client {

        public static void renderItem(ItemStack stack, int x, int y, int screenWidth, int screenHeight, int mouseX, int mouseY)
        {
            Minecraft mc = Minecraft.getMinecraft();
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);

            GL11.glTranslatef(0.0F, 0.0F, 32.0F);
            renderItem.zLevel = 100.0F;

            renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y);
            renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y,
                    stack.stackSize > 1 ? String.valueOf(stack.stackSize) : "");
            renderItem.zLevel = 0.0F;

            if (mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16) {
//                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                renderToolTip(stack, mouseX, mouseY, screenWidth, screenHeight);
//                GL11.glEnable(GL11.GL_SCISSOR_TEST);
            }

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }


        public static void renderItem3D(ItemStack itemStack, int x, int y, int scale, float rotY)
        {
            Minecraft mc = Minecraft.getMinecraft();

            GL11.glPushMatrix();
            GL11.glPushAttrib(8256);
            GL11.glTranslatef(x, y, 400.0F);
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(180f, 1f, 0f, 0f);
            GL11.glRotatef(rotY, 0f, 1f, 0f);
            Block block = Block.getBlockFromItem(itemStack.getItem());
            if (block != null) GL11.glRotatef(10f, 1f, 0f, 0f);
            GL11.glDepthMask(false);
            if (block != null) {
                GL11.glEnable(GL11.GL_LIGHTING);

                GuiUtil.setBrightness(1.2F, 1.9F, 1.2F, -2.2F, 1.9F, 1.2F,
                        0.6F,
                        0.3F,
                        0.8F);
            }
            itemRenderer.renderItem(mc.thePlayer, itemStack, 0);
            GL11.glDepthMask(true);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

        private static void renderToolTip(ItemStack p_146285_1_, int p_146285_2_, int p_146285_3_, int width, int height)
        {
            Minecraft mc = Minecraft.getMinecraft();
            List list = p_146285_1_.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);

            for (int k = 0; k < list.size(); ++k) {
                if (k == 0) {
                    list.set(k, p_146285_1_.getRarity().rarityColor + (String) list.get(k));
                } else {
                    list.set(k, EnumChatFormatting.GRAY + (String) list.get(k));
                }
            }

            FontRenderer font = p_146285_1_.getItem().getFontRenderer(p_146285_1_);
            drawHoveringText(list, p_146285_2_, p_146285_3_, (font == null ? mc.fontRenderer : font), width, height);
        }


        private static void drawHoveringText(List p_146283_1_, int p_146283_2_, int p_146283_3_, FontRenderer font, int width, int height)
        {
            if (!p_146283_1_.isEmpty()) {
                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                RenderHelper.disableStandardItemLighting();
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
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

                if (j2 + k > width) {
                    j2 -= 28 + k;
                }

                if (k2 + i1 + 6 > height) {
                    k2 = height - i1 - 6;
                }

                renderItem.zLevel = 300.0F;
                int j1 = -267386864;
                drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
                drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
                drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
                drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
                drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
                int k1 = 1347420415;
                int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
                drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
                drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
                drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
                drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);
                for (int i2 = 0; i2 < p_146283_1_.size(); ++i2) {
                    String s1 = (String) p_146283_1_.get(i2);
                    font.drawStringWithShadow(s1, j2, k2, -1);

                    if (i2 == 0) {
                        k2 += 2;
                    }

                    k2 += 10;
                }

                renderItem.zLevel = 0.0F;
                GL11.glEnable(GL11.GL_LIGHTING);
                RenderHelper.enableStandardItemLighting();
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            }
        }

        private static void drawGradientRect(int p_73733_1_, int p_73733_2_, int p_73733_3_, int p_73733_4_, int p_73733_5_, int p_73733_6_)
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
            tessellator.addVertex((double) p_73733_3_, (double) p_73733_2_, (double) renderItem.zLevel);
            tessellator.addVertex((double) p_73733_1_, (double) p_73733_2_, (double) renderItem.zLevel);
            tessellator.setColorRGBA_F(f5, f6, f7, f4);
            tessellator.addVertex((double) p_73733_1_, (double) p_73733_4_, (double) renderItem.zLevel);
            tessellator.addVertex((double) p_73733_3_, (double) p_73733_4_, (double) renderItem.zLevel);
            tessellator.draw();
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

}
