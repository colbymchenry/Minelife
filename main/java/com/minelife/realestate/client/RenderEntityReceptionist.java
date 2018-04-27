package com.minelife.realestate.client;

import com.minelife.Minelife;
import com.minelife.realestate.EntityReceptionist;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderEntityReceptionist extends RenderBiped {

    private static ResourceLocation[] textures = new ResourceLocation[]{
            new ResourceLocation(Minelife.MOD_ID, "textures/entity/receptionist/receptionist_0.png"),
            new ResourceLocation(Minelife.MOD_ID, "textures/entity/receptionist/receptionist_1.png"),
            new ResourceLocation(Minelife.MOD_ID, "textures/entity/receptionist/receptionist_2.png")
    };

    public RenderEntityReceptionist(RenderManager renderManager) {
        super(renderManager, new ModelPlayer(0.0F, false), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        EntityReceptionist jobNPC = (EntityReceptionist) entity;
        return textures[jobNPC.getSkin()];
    }

}