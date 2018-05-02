package com.minelife.airdrop;

import com.minelife.Minelife;
import com.minelife.util.client.render.ModelPlayerCustom;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderBandit extends RenderBiped {

    private static ResourceLocation[] textures = new ResourceLocation[]{
            new ResourceLocation(Minelife.MOD_ID, "textures/entity/bandit/bandit0.png"),
            new ResourceLocation(Minelife.MOD_ID, "textures/entity/bandit/bandit1.png"),
            new ResourceLocation(Minelife.MOD_ID, "textures/entity/bandit/bandit2.png"),
            new ResourceLocation(Minelife.MOD_ID, "textures/entity/bandit/bandit3.png")
    };

    public RenderBandit(RenderManager renderManager) {
        super(renderManager, new ModelPlayerCustom(0.0F, false), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return textures[((EntityBandit) entity).getSkin()];
    }

}