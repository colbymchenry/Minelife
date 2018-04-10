package com.minelife.util.client.render;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class AdjustPlayerModelEvent extends Event {

    private float ageInTicks;
    private ModelPlayer model;
    private EntityPlayer player;

    public AdjustPlayerModelEvent(ModelPlayer model, float ageInTicks, EntityPlayer player) {
        this.model = model;
        this.ageInTicks = ageInTicks;
        this.player = player;
    }

    public ModelPlayer getModel() {
        return model;
    }

    public float getAgeInTicks() {
        return ageInTicks;
    }

    public EntityPlayer getPlayer() {
        return player;
    }
}
