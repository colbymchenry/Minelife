package com.minelife.police.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minelife.MLProxy;
import com.minelife.core.event.EntityDismountEvent;
import com.minelife.emt.ModEMT;
import com.minelife.emt.entity.EntityEMT;
import com.minelife.essentials.Location;
import com.minelife.essentials.server.EventTeleport;
import com.minelife.essentials.server.commands.Spawn;
import com.minelife.police.EntityCop;
import com.minelife.police.ModPolice;
import com.minelife.util.MLConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.io.File;
import java.util.List;
import java.util.Random;

public class ServerProxy extends MLProxy {

    public static MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        Prison.initPrisons();
        config = new MLConfig("police");
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ModPolice.itemHandcuff);
        MinecraftForge.EVENT_BUS.register(ModPolice.itemHandcuffKey);
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
        if(event.getEntity() instanceof EntityEMT) {
            com.minelife.emt.ServerProxy.spawnEMTs(event.getEntity().world);
            EntityEMT emt = (EntityEMT) event.getEntity();
            if(emt.getRevivingPlayer() != null) {
                ModEMT.requestEMT(emt.getRevivingPlayer());
            }
            return;
        }

        if(event.getEntity() instanceof EntityCop) spawnCops(event.getEntity().world);

        if (!(event.getEntity() instanceof EntityPlayer)) return;

        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityCop.getNearbyPolice(event.getEntityLiving().getEntityWorld(), event.getSource().getTrueSource().getPosition()).forEach(officer -> {
                if (officer.getEntitySenses().canSee(event.getSource().getTrueSource())) {
                    officer.setAttackTarget((EntityPlayer) event.getSource().getTrueSource());
                    officer.getPoliceAI().setKillerPlayer(event.getSource().getTrueSource().getUniqueID());
                }
            });
        }

        event.setCanceled(true);
        ((EntityPlayer) event.getEntity()).setHealth(10);
        ModPolice.setUnconscious((EntityPlayer) event.getEntity(), true, false);
        ModEMT.requestEMT((EntityPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onDamage(LivingDamageEvent event) {
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) return;
        if (!(event.getEntity() instanceof EntityPlayer) && !(event.getEntity() instanceof EntityCop)) return;

        EntityCop.getNearbyPolice(event.getEntityLiving().getEntityWorld(), event.getSource().getTrueSource().getPosition()).forEach(officer -> {
            if (officer.getEntitySenses().canSee(event.getSource().getTrueSource())) {
                officer.setAttackTarget((EntityPlayer) event.getSource().getTrueSource());
                officer.getPoliceAI().setAggressivePlayer(event.getSource().getTrueSource().getUniqueID());
            }
        });

    }

    private static void deletePlayer(EntityPlayer player) {
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

            ModPolice.setUnconscious(event.player, false, false);

            Location spawn = Spawn.GetSpawn();
            if (spawn != null)
                event.player.setPositionAndUpdate(spawn.getX(), spawn.getY(), spawn.getZ());
            else {
                BlockPos worldSpawn = event.player.world.getSpawnPoint();
                event.player.setPositionAndUpdate(worldSpawn.getX(), worldSpawn.getY(), worldSpawn.getZ());
            }
        }
    }

    @SubscribeEvent
    public void onTeleport(EventTeleport event) {
       if(Prison.getPrison(event.getPlayer().getPosition()) != null || event.getPlayer().isRiding()) {
           event.setCanceled(true);
           if(event.getPlayer().isRiding()) {
               event.getPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "You are under arrest. Teleportation cancelled."));
           } else {
               event.getPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "You are in prison. Teleportation cancelled."));
           }
       }
    }

    private static Random random = new Random();
    private static long nextCheck = 0L;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (nextCheck <= System.currentTimeMillis()) {
            // initial setup
            if(nextCheck == 0) {
                cleanCops(FMLServerHandler.instance().getServer().worlds[0]);
                spawnCops(FMLServerHandler.instance().getServer().worlds[0]);
                nextCheck = System.currentTimeMillis() + 60000L;
                return;
            }

            nextCheck = System.currentTimeMillis() + 60000L;

            List<EntityCop> cops = getCopsForWorld(FMLServerHandler.instance().getServer().worlds[0]);
            if (cops.size() > config.getInt("MaxCops", 30)) {
                int toDelete = cops.size() - config.getInt("MaxCops", 30);
                for (int i = 0; i < toDelete; i++) {
                    deleteRandomCop(FMLServerHandler.instance().getServer().worlds[0]);
                }
            }
        }
    }

    public static void spawnCops(World world) {
        int maxCops = config.getInt("MaxCops", 30);
        List<String> spawns = config.getStringList("PoliceSpawnPoints") != null ? config.getStringList("PoliceSpawnPoints") : null;
        int policePerSpawn = spawns.isEmpty() ? 0 : maxCops / spawns.size();

        for (String spawn : spawns) {
            String[] data = spawn.split(",");
            BlockPos pos = new BlockPos(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            for (int i = 0; i < policePerSpawn; i++) {
                EntityCop cop = new EntityCop(world);
                cop.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                world.spawnEntity(cop);
            }
        }
    }

    public static void cleanCops(World world) {
        getCopsForWorld(world).forEach(Entity::setDead);
    }

    public static EntityCop deleteRandomCop(World world) {
        List<EntityCop> cops = getCopsForWorld(world);
        EntityCop cop = cops.get(random.nextInt(cops.size()));
        cops.remove(cop);
        cop.setDead();
        return cop;
    }

    public static List<EntityCop> getCopsForWorld(World world) {
        List<EntityCop> cops = Lists.newArrayList();

        for (Entity entity : world.loadedEntityList) {
            if (entity instanceof EntityCop) {
                cops.add((EntityCop) entity);
            }
        }

        return cops;
    }

}
