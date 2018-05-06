package com.minelife.police.server;

import com.google.common.collect.Sets;
import com.minelife.MLProxy;
import com.minelife.emt.ModEMT;
import com.minelife.essentials.Location;
import com.minelife.essentials.server.commands.Spawn;
import com.minelife.police.EntityCop;
import com.minelife.police.ModPolice;
import com.minelife.util.MLConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;

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
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.getRidingEntity() != null && player.getRidingEntity() instanceof EntityCop)
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityCop.getNearbyPolice(event.getEntityLiving().getEntityWorld(), event.getSource().getTrueSource().getPosition()).forEach(officer -> {
                officer.setAttackTarget((EntityPlayer) event.getSource().getTrueSource());
                officer.getPoliceAI().setKillerPlayer(event.getSource().getImmediateSource().getUniqueID());
            });
        }

        event.setCanceled(true);
        ((EntityPlayer) event.getEntity()).setHealth(10);
        ModPolice.setUnconscious((EntityPlayer) event.getEntity(), true);
        ModEMT.requestEMT((EntityPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onDamage(LivingDamageEvent event) {
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) return;
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        EntityCop.getNearbyPolice(event.getEntityLiving().getEntityWorld(), event.getSource().getTrueSource().getPosition()).forEach(officer -> {
            officer.setAttackTarget((EntityPlayer) event.getSource().getTrueSource());
            officer.getPoliceAI().setAggressivePlayer(event.getSource().getImmediateSource().getUniqueID());
        });

    }

    private void deletePlayer(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if (player.inventory.getStackInSlot(i) != ItemStack.EMPTY) {
                player.entityDropItem(player.inventory.getStackInSlot(i), 0.5f);
            }
        }

        File file = new File(System.getProperty("user.dir") + "/world/playerdata", player.getUniqueID().toString() + ".dat");
        file.delete();
    }

    @SubscribeEvent
    public void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (ModPolice.isUnconscious(event.player)) deletePlayer(event.player);
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (ModPolice.isUnconscious(event.player) && System.currentTimeMillis() >= event.player.getEntityData().getLong("UnconsciousTime")) {
            for (int i = 0; i < event.player.inventory.getSizeInventory(); i++) {
                if (event.player.inventory.getStackInSlot(i) != ItemStack.EMPTY) {
                    event.player.entityDropItem(event.player.inventory.getStackInSlot(i), 0.5f);
                }
            }

            ModPolice.setUnconscious(event.player, false);

            Location spawn = Spawn.GetSpawn();
            if (spawn != null)
                event.player.setPositionAndUpdate(spawn.getX(), spawn.getY(), spawn.getZ());
            else {
                BlockPos worldSpawn = event.player.world.getSpawnPoint();
                event.player.setPositionAndUpdate(worldSpawn.getX(), worldSpawn.getY(), worldSpawn.getZ());
            }
        }
    }


}
