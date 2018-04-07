package com.minelife.guns.turret;

import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;

public enum EnumMob {
    Bat (EntityBat.class),
    Chicken (EntityChicken.class),
    Cow (EntityCow.class),
    Horse (EntityHorse.class),
    Mooshroom (EntityMooshroom.class),
    Ocelot (EntityOcelot.class),
    Pig (EntityPig.class),
    Sheep (EntitySheep.class),
    Squid (EntitySquid.class),
    Villager (EntityVillager.class),
    Wolf (EntityWolf.class),
    Blaze (EntityBlaze.class),
    CaveSpider (EntityCaveSpider.class),
    Creeper (EntityCreeper.class),
    Enderman (EntityEnderman.class),
    Ghast (EntityGhast.class),
    GiantZombie (EntityGiantZombie.class),
    Golem (EntityGolem.class),
    IronGolem (EntityIronGolem.class),
    MagmaCube (EntityMagmaCube.class),
    PigZombie (EntityPigZombie.class),
    Silverfish (EntitySilverfish.class),
    Skeleton (EntitySkeleton.class),
    Slime (EntitySlime.class),
    Snowman (EntitySnowman.class),
    Spider (EntitySpider.class),
    Witch (EntityWitch.class),
    Zombie (EntityZombie.class);

    private Class MobClass;
    
    EnumMob(Class MobClass) {
        this.MobClass = MobClass;
    }

    public Class getMobClass() {
        return MobClass;
    }
}
