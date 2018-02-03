package com.minelife.drug.client.render;

import com.minelife.Minelife;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ItemCocaLeafRenderer implements IItemRenderer {

    protected static Minecraft mc = Minecraft.getMinecraft();
    protected static ResourceLocation texture = new ResourceLocation(Minelife.MOD_ID + ":textures/items/coca_leaf.png");
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        Color FAR = new Color(237, 234, 0);
        Color CLOSE = new Color(0, 237, 21);
        int[] transition_color = GuiUtil.transition(FAR, CLOSE, item.getItemDamage() / 80.0);
        GL11.glColor4f(transition_color[0] / 255f, transition_color[1] / 255f, transition_color[2] / 255f, 1f);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        if (type == ItemRenderType.INVENTORY) {
            mc.getTextureManager().bindTexture(texture);
            GuiUtil.drawImage(0, 0, 16, 16);
        }
        else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glPushMatrix();
            {
                GL11.glRotatef(70f, 0f, 1f, 0f);
                GL11.glTranslatef(-0.7f, 0.8f, 0.2f);
                GuiUtil.render_item_in_world(mc, item);
            }
            GL11.glPopMatrix();
        } else {
            GuiUtil.render_item_in_world(mc, item);
        }
    }

}
