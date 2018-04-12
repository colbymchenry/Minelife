package com.minelife.util.client.render;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class ModelPlayerCustom extends ModelPlayer {

    public ModelPlayerCustom(float modelSize, boolean smallArmsIn) {
        super(modelSize, smallArmsIn);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        MinecraftForge.EVENT_BUS.post(new AdjustPlayerModelEvent(this, ageInTicks, (EntityPlayer) entityIn));
    }
}
