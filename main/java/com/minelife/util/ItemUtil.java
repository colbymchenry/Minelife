package com.minelife.util;

import com.minelife.util.client.GuiUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

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

        public static int[] renderItem(ItemStack stack, int x, int y, int screenWidth, int screenHeight, int mouseX, int mouseY)
        {
            Minecraft mc = Minecraft.getMinecraft();
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL11.GL_BLEND);
            renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y);
            renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y,
                    stack.stackSize > 1 ? String.valueOf(stack.stackSize) : "");

            int[] toReturn = new int[2];

            if (mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                toReturn = renderToolTip(stack, mouseX, mouseY, screenWidth, screenHeight);
            }

            RenderHelper.disableStandardItemLighting();
            return toReturn;
        }

        public static int[] renderToolTip(ItemStack stack, int x, int y, int width, int height)
        {
            Minecraft mc = Minecraft.getMinecraft();
            List<String> list = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);

            for (int i = 0; i < list.size(); ++i) {
                if (i == 0) {
                    list.set(i, stack.getRarity().rarityColor + (String) list.get(i));
                } else {
                    list.set(i, EnumChatFormatting.GRAY + (String) list.get(i));
                }
            }

            FontRenderer font = stack.getItem().getFontRenderer(stack);
            return GuiUtil.drawHoveringText(list, x, y, width, height, 100.0F);
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
    }

}
