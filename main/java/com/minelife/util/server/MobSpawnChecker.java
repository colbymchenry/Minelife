package com.minelife.util.server;

import com.google.common.collect.Maps;
import com.minelife.airdrop.EntityBandit;
import com.minelife.jobs.EntityJobNPC;
import com.minelife.pvplogger.EntityPlayerTracker;
import com.minelife.realestate.EntityReceptionist;
import de.maxhenkel.car.entity.car.base.EntityCarNumberPlateBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

public class MobSpawnChecker {

    private static Map<Class<? extends EntityLivingBase>, Integer> amountSpawned = Maps.newHashMap();

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityHorse || event.getEntity() instanceof EntityPlayer ||
                event.getEntity() instanceof EntityJobNPC || event.getEntity() instanceof EntityReceptionist ||
                event.getEntity() instanceof EntityPlayerTracker || event.getEntity() instanceof EntityGuardian
                || event.getEntity() instanceof EntityElderGuardian ||
                event.getEntity() instanceof EntityArmorStand || event.getEntity() instanceof EntityCarNumberPlateBase ||
                event.getEntity() instanceof EntityBandit) return;

        if (!(event.getEntity() instanceof EntityLivingBase)) return;

        int rand = event.getWorld().rand.nextInt(100);

        if (rand < 95) {
            event.getEntity().setDead();
            return;
        }

        if (!amountSpawned.containsKey((Class<? extends EntityLivingBase>) event.getEntity().getClass())) {
            amountSpawned.put((Class<? extends EntityLivingBase>) event.getEntity().getClass(), 1);
        } else {
            if (amountSpawned.get((Class<? extends EntityLivingBase>) event.getEntity().getClass()) > 64)
                event.getEntity().setDead();
            amountSpawned.put((Class<? extends EntityLivingBase>) event.getEntity().getClass(), amountSpawned.get((Class<? extends EntityLivingBase>) event.getEntity().getClass()) + 1);
        }
    }

}
