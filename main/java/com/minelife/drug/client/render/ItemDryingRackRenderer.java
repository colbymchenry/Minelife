package com.minelife.drug.client.render;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class ItemDryingRackRenderer implements IItemRenderer {

    ResourceLocation texture;
    ResourceLocation objModelLocation;
    IModelCustom model;

    public ItemDryingRackRenderer() {
        texture = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/drying_rack.png");
        objModelLocation = new ResourceLocation(Minelife.MOD_ID, "models/drying_rack.obj");
        model = AdvancedModelLoader.loadModel(objModelLocation);
    }

    @Override
    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPushMatrix();
        {
            if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
                GL11.glRotatef(270f, 0, 1, 0);
                GL11.glTranslatef(0.3f, 0f, -0.5f);
            }

            if(type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glScalef(0.6f, 0.6f, 0.6f);
                GL11.glTranslatef(0f, -0.75f, 0f);
            }

            if(type == IItemRenderer.ItemRenderType.EQUIPPED) {
                GL11.glScalef(1.5f, 1.5f, 1.5f);
                GL11.glTranslatef(-0.3f, 0.2f, -0.4f);
            }

            model.renderAll();
        }
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

}