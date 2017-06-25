package com.minelife.gun.client;

import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.client.guns.ItemGunClient;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderGun implements IItemRenderer {

    private ItemGunClient client;

    public RenderGun(ItemGun gun) {
        this.client = gun.getClientHandler();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return client.handleRenderType(item, type);
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return client.shouldUseRenderHelper(type, item, helper);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(client.getTexture());
        GL11.glDisable(GL11.GL_CULL_FACE);

        client.renderItem(type, item, data);

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

}