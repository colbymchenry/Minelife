package com.minelife.police.computer;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class ItemPoliceComputerRenderer implements IItemRenderer {

    private ResourceLocation texture;
    private ResourceLocation objModelLocation;
    private IModelCustom model;

    public ItemPoliceComputerRenderer() {
        texture = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/police_computer.png");
        objModelLocation = new ResourceLocation(Minelife.MOD_ID, "models/police_computer.obj");
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
                GL11.glRotatef(180f, 0, 1, 0);
                GL11.glTranslatef(0.6f, 0.8f, -1f);
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

