package com.minelife.util.client.render;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class AdjustPlayerModelEvent extends Event {

    private float ageInTicks;
    private ModelPlayerCustom model;
    private EntityPlayer player;

    public AdjustPlayerModelEvent(ModelPlayerCustom model, float ageInTicks, EntityPlayer player) {
        this.model = model;
        this.ageInTicks = ageInTicks;
        this.player = player;
    }

    public ModelPlayerCustom getModel() {
        return model;
    }

    public float getAgeInTicks() {
        return ageInTicks;
    }

    public EntityPlayer getPlayer() {
        return player;
    }
}
