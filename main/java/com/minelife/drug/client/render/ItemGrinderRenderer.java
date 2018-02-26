package com.minelife.drug.client.render;

import com.minelife.Minelife;
import com.minelife.drug.item.ItemGrinder;
import com.minelife.util.client.GuiUtil;
import com.minelife.util.client.render.MLItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class ItemGrinderRenderer implements IItemRenderer {

    protected static Minecraft mc = Minecraft.getMinecraft();
    protected static ResourceLocation texture = new ResourceLocation(Minelife.MOD_ID + ":textures/items/coca_leaf.png");

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
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        IIcon icon = item.hasTagCompound() && item.getTagCompound().hasKey("color_index") ?
                ItemGrinder.icons[item.getTagCompound().getInteger("color_index")] : ItemGrinder.icons[0];

        if (type == ItemRenderType.INVENTORY) {
            GL11.glPushMatrix();
            {
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glScalef(2, 2, 2);
                GL11.glTranslatef(0, -0.5f, 0);
                GL11.glRotatef(45f, 0, 1, 0);
                GuiUtil.render_item_in_world(mc, item, icon);
                GL11.glEnable(GL11.GL_LIGHTING);
            }
            GL11.glPopMatrix();
        } else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glPushMatrix();
            {
                GL11.glRotatef(70f, 0f, 1f, 0f);
                GL11.glTranslatef(-0.7f, 0.8f, 0.2f);
                GuiUtil.render_item_in_world(mc, item, icon);
            }
            GL11.glPopMatrix();
        } else {
            GuiUtil.render_item_in_world(mc, item, icon);
        }


    }
}
