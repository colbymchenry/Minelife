package com.minelife.gun.server;

import com.minelife.gun.item.guns.ItemGun;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class BulletHitEvent extends Event {

    private EntityLivingBase entityShooter;
    private EntityLivingBase entityDamaged;
    private ItemStack gun;
    private int damage;
    public double x, y, z;

    public BulletHitEvent(EntityLivingBase entityShooter, EntityLivingBase entityDamaged, ItemStack gun) {
        this.entityShooter = entityShooter;
        this.entityDamaged = entityDamaged;
        this.gun = gun;
    }

    public BulletHitEvent(EntityLivingBase entityShooter, double x, double y, double z, ItemStack gun) {
        this.entityShooter = entityShooter;
        this.gun = gun;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public EntityLivingBase getEntityShooter() {
        return entityShooter;
    }

    public EntityLivingBase getEntityDamaged() {
        return entityDamaged;
    }

    public ItemStack getGun() {
        return gun;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
