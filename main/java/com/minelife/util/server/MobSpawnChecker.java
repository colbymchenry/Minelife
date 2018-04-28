package com.minelife.util.server;

import com.google.common.collect.Maps;
import com.minelife.jobs.EntityJobNPC;
import com.minelife.realestate.EntityReceptionist;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

public class MobSpawnChecker {

    private static Map<Class<? extends EntityLivingBase>, Integer> amountSpawned = Maps.newHashMap();

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if(event.getEntity() instanceof EntityHorse || event.getEntity() instanceof EntityPlayer ||
                event.getEntity() instanceof EntityJobNPC || event.getEntity() instanceof EntityReceptionist) return;

        if(!(event.getEntity() instanceof EntityLivingBase)) return;

        int rand = event.getWorld().rand.nextInt(100);

        if(rand < 95) {
            event.setCanceled(true);
            return;
        }

        if(!amountSpawned.containsKey((Class<? extends EntityLivingBase>) event.getEntity().getClass())) {
            amountSpawned.put((Class<? extends EntityLivingBase>) event.getEntity().getClass(), 1);
        } else {
            if(amountSpawned.get((Class<? extends EntityLivingBase>) event.getEntity().getClass()) > 64) event.setCanceled(true);
            amountSpawned.put((Class<? extends EntityLivingBase>) event.getEntity().getClass(), amountSpawned.get((Class<? extends EntityLivingBase>) event.getEntity().getClass()) + 1);
        }
    }

}
