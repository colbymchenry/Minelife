package com.minelife.emt.entity;

import com.minelife.Minelife;
import com.minelife.util.client.render.ModelPlayerCustom;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderEntityEMT extends RenderBiped {

    private static ResourceLocation texture = new ResourceLocation(Minelife.MOD_ID, "textures/entity/job/emt.png");

    public RenderEntityEMT(RenderManager renderManager) {
        super(renderManager, new ModelPlayerCustom(0.0F, false), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return texture;
    }

}