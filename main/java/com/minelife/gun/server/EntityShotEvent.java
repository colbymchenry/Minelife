package com.minelife.gun.server;

import com.minelife.gun.BaseGun;
import com.minelife.gun.GunRegistry;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class EntityShotEvent extends Event {

    private EntityLivingBase entityShooter;
    private EntityLivingBase entityDamaged;
    private ItemStack gun;
    private int damage;

    public EntityShotEvent(EntityLivingBase entityShooter, EntityLivingBase entityDamaged, ItemStack gun) {
        this.entityShooter = entityShooter;
        this.entityDamaged = entityDamaged;
        this.gun = gun;

        if (this.isCanceled()) return;

        if(BaseGun.getCurrentClipHoldings(gun) < 1) return;

        this.damage = GunRegistry.get(gun.getItem().getClass()).getDamage();

        entityDamaged.attackEntityFrom(DamageSource.causeMobDamage(entityShooter), this.damage);
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

}
