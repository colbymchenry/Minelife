package com.minelife.util.client.render;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class AdjustPlayerModelEvent extends Event {

    private float ageInTicks;
    private ModelPlayer model;
    private EntityLivingBase entityLiving;

    public AdjustPlayerModelEvent(ModelPlayer model, float ageInTicks, EntityLivingBase player) {
        this.model = model;
        this.ageInTicks = ageInTicks;
        this.entityLiving = player;
    }

    public ModelPlayer getModel() {
        return model;
    }

    public float getAgeInTicks() {
        return ageInTicks;
    }

    public EntityLivingBase getPlayer() {
        return entityLiving;
    }
}
