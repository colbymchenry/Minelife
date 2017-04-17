package com.minelife.gun.client;

import com.minelife.gun.ItemGun;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderGun implements IItemRenderer {

    private ItemGun gun;

    public RenderGun(ItemGun gun) {
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
