package com.minelife.police.server;

import com.google.common.collect.Sets;
import com.minelife.MLProxy;
import com.minelife.police.EntityCop;
import com.minelife.util.MLConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerProxy extends MLProxy {

    public static MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        Prison.initPrisons();
        config = new MLConfig("police");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onHit(LivingAttackEvent event) {
        if(event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if(player.getRidingEntity() != null && player.getRidingEntity() instanceof EntityCop) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if(!(event.getSource().getTrueSource() instanceof EntityPlayer)) return;
        if(!(event.getEntity() instanceof EntityPlayer)) return;

        EntityCop.getNearbyPolice(event.getEntityLiving().getEntityWorld(), event.getSource().getTrueSource().getPosition()).forEach(officer -> {
            officer.setAttackTarget((EntityPlayer) event.getSource().getTrueSource());
            officer.getPoliceAI().setKillerPlayer(event.getSource().getImmediateSource().getUniqueID());
        });
    }

    @SubscribeEvent
    public void onDamage(LivingDamageEvent event) {
        if(!(event.getSource().getTrueSource() instanceof EntityPlayer)) return;
        if(!(event.getEntity() instanceof EntityPlayer)) return;

        EntityCop.getNearbyPolice(event.getEntityLiving().getEntityWorld(), event.getSource().getTrueSource().getPosition()).forEach(officer -> {
            officer.setAttackTarget((EntityPlayer) event.getSource().getTrueSource());
            officer.getPoliceAI().setAggressivePlayer(event.getSource().getImmediateSource().getUniqueID());
        });

    }


}
