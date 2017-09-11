package com.minelife.gun.server;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class ShootBulletEvent extends Event {

    private EntityLivingBase entityShooter;
    private ItemStack gun;

    public ShootBulletEvent(EntityLivingBase entityShooter, ItemStack gun) {
        this.entityShooter = entityShooter;
        this.gun = gun;
    }

    public EntityLivingBase getEntityShooter() {
        return entityShooter;
    }

    public ItemStack getGun() {
        return gun;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}