package com.minelife.capes.server;

import com.minelife.MLItems;
import com.minelife.util.Color;
import com.minelife.util.FireworkBuilder;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class DeathListener {

    @SubscribeEvent
    public void deathEvent(LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityCreeper) {
            if (event.source.getEntity() instanceof EntitySkeleton) {
                EntityCreeper creeper = (EntityCreeper) event.entityLiving;
                creeper.dropItem(MLItems.cape, 1);

                ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                        new int[]{Color.RED.asRGB(), Color.ORANGE.asRGB()}, new int[]{Color.YELLOW.asRGB(), Color.ORANGE.asRGB()}).getStack(1);

                EntityFireworkRocket ent = new EntityFireworkRocket(creeper.worldObj, creeper.posX, creeper.posY + 2, creeper.posZ, fireworkStack);
                creeper.worldObj.spawnEntityInWorld(ent);
                EntityFireworkRocket ent1 = new EntityFireworkRocket(creeper.worldObj, creeper.posX, creeper.posY + 2, creeper.posZ, fireworkStack);
                creeper.worldObj.spawnEntityInWorld(ent1);
            }
        }
    }

}
