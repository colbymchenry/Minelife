package com.minelife.pvplogger;

import com.minelife.util.SkinHelper;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderEntityPlayerTracker extends RenderBiped {

    public RenderEntityPlayerTracker(RenderManager renderManager) {
        super(renderManager, new ModelPlayer(0.0F, false), 0.5F);
        this.addLayer(new LayerBipedArmor(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        EntityPlayerTracker playerTracker = (EntityPlayerTracker) entity;
        return SkinHelper.loadSkin(playerTracker.getPlayerID());
    }

}