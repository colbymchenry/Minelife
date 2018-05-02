package com.minelife.police.client;

import com.minelife.Minelife;
import com.minelife.util.client.render.ModelPlayerCustom;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderCop extends RenderBiped {

    private static ResourceLocation[] textures = new ResourceLocation[]{
            new ResourceLocation(Minelife.MOD_ID, "textures/entity/job/police.png"),
            new ResourceLocation(Minelife.MOD_ID, "textures/entity/receptionist/receptionist_1.png"),
            new ResourceLocation(Minelife.MOD_ID, "textures/entity/receptionist/receptionist_2.png")
    };

    public RenderCop(RenderManager renderManager) {
        super(renderManager, new ModelPlayerCustom(0.0F, false), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return textures[0];
    }

}