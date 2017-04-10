package com.minelife.gun.client;

import com.minelife.gun.Ammo;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderAmmo implements IItemRenderer {

    private Ammo ammo;

    public RenderAmmo(Ammo ammo) {
        this.ammo = ammo;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return ammo.handleRenderType(item, type);
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return ammo.shouldUseRenderHelper(type, item, helper);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ammo.texture);
        GL11.glDisable(GL11.GL_CULL_FACE);

        ammo.renderItem(type, item, data);

        GL11.glEnable(GL11.GL_CULL_FACE);
    }
}
