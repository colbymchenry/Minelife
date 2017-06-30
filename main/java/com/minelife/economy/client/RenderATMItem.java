package com.minelife.economy.client;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class RenderATMItem implements IItemRenderer {

    ResourceLocation texture;
    ResourceLocation objModelLocation;
    IModelCustom model;

    public RenderATMItem() {
        texture = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/atm.png");
        objModelLocation = new ResourceLocation(Minelife.MOD_ID, "models/atm.obj");
        model = AdvancedModelLoader.loadModel(objModelLocation);
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
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPushMatrix();
        {
            if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
                GL11.glRotatef(270f, 0, 1, 0);
                GL11.glTranslatef(0.3f, 0f, -0.5f);
            }

            if(type == ItemRenderType.INVENTORY) {
                GL11.glScalef(0.6f, 0.6f, 0.6f);
                GL11.glTranslatef(0.5f, -1.25f, 0f);
            }

            if(type == ItemRenderType.EQUIPPED) {
                GL11.glScalef(1.5f, 1.5f, 1.5f);
                GL11.glTranslatef(0.5f, 0f, 0.1f);
            }

            model.renderAll();
        }
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

}