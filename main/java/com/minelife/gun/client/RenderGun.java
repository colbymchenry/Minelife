package com.minelife.gun.client;

import com.minelife.Minelife;
import com.minelife.gun.Gun;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class RenderGun implements IItemRenderer {

    private Gun gun;

    public RenderGun(Gun gun) {
        this.gun = gun;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return gun.handleRenderType(item, type);
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return gun.shouldUseRenderHelper(type, item, helper);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(gun.texture);
        GL11.glDisable(GL11.GL_CULL_FACE);

        gun.renderItem(type, item, data);

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

}
