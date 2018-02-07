package com.minelife.economy.cash;

import com.minelife.MLBlocks;
import com.minelife.util.client.GuiUtil;
import com.minelife.util.client.render.MLItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderCashBlockItem implements IItemRenderer {

    private static final ItemStack stack = new ItemStack(Blocks.wooden_slab);

    public RenderCashBlockItem() {
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.6f, 0f, 0f);

        if(type == ItemRenderType.EQUIPPED_FIRST_PERSON)
            GL11.glTranslatef(0f, 0.3f, 0f);

        GL11.glRotatef(90f, 1f, 0f, 0f);

        GuiUtil.render_item_in_world(Minecraft.getMinecraft(), stack);
        GL11.glPopMatrix();

        if(type == ItemRenderType.INVENTORY) {
//            RenderHelper.enableGUIStandardItemLighting();
//            RenderHelper.enableStandardItemLighting();
        }
    }

}
