package com.minelife.guns.client;

import com.minelife.Minelife;
import com.minelife.guns.EntityDynamite;
import com.minelife.guns.ModGuns;
import com.minelife.util.client.GuiFakeInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderDynamite extends Render<EntityDynamite> {

    private static ResourceLocation dynamiteTexture = new ResourceLocation(Minelife.MOD_ID, "textures/item/dynamite.png");
    private static ItemStack dynamiteStack = new ItemStack(ModGuns.itemDynamite);

    public RenderDynamite(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void doRender(EntityDynamite entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if(!entity.getEntityData().getBoolean("client")) return;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        GuiFakeInventory.renderItem(Minecraft.getMinecraft(), dynamiteStack, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();
        if (System.currentTimeMillis() % 50 == 0)
            entity.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, entity.posX, entity.posY + 0.3, entity.posZ, entity.motionX, 0.1, entity.motionZ);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityDynamite entity) {
        return dynamiteTexture;
    }
}