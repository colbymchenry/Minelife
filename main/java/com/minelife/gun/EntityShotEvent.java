package com.minelife.gun;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EntityShotEvent extends Event {

    private EntityLivingBase entityShooter;
    private EntityLivingBase entityDamaged;
    private ItemStack gun;

    public EntityShotEvent(EntityLivingBase entityShooter, EntityLivingBase entityDamaged, ItemStack gun) {
        this.entityShooter = entityShooter;
        this.entityDamaged = entityDamaged;
        this.gun = gun;
    }
}
