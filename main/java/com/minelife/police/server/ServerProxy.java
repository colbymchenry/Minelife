package com.minelife.police.server;

import com.google.common.collect.Lists;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.emt.ModEMT;
import com.minelife.emt.entity.EntityEMT;
import com.minelife.essentials.Location;
import com.minelife.essentials.server.EventTeleport;
import com.minelife.essentials.server.commands.Spawn;
import com.minelife.police.ArrestedEffect;
import com.minelife.police.ItemHandcuff;
import com.minelife.police.ModPolice;
import com.minelife.police.Prisoner;
import com.minelife.police.cop.EntityCop;
import com.minelife.util.MLConfig;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.PlayerHelper;
import com.minelife.util.StringHelper;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    public static MLConfig config;
    public static Database database;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        try {
            database = new SQLite(Logger.getLogger("Minecraft"), "[Police]", Minelife.getDirectory().getAbsolutePath(), "police");
            database.open();
            database.query("CREATE TABLE IF NOT EXISTS prisoners (uuid VARCHAR(36), charges TEXT, timeServed LONG)");
            config = new MLConfig("police");
            config.save();
            Prison.initPrisons();
            Prisoner.initPrisoners();
            MinecraftForge.EVENT_BUS.register(this);
            MinecraftForge.EVENT_BUS.register(ArrestedEffect.INSTANCE);
            MinecraftForge.EVENT_BUS.register(ModPolice.itemHandcuffKey);
            MinecraftForge.EVENT_BUS.register(new Prisoner.Listener());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (event.getEntity() instanceof EntityCop) {
            event.setCanceled(true);
            EntityCop cop = (EntityCop) event.getEntity();
            event.getEntity().setPosition(cop.getSpawnPoint().getX() + 0.5, cop.getSpawnPoint().getY() + 0.5, cop.getSpawnPoint().getZ() + 0.5);
            cop.setHealth(20);
        }

        if (!(event.getEntity() instanceof EntityPlayer) && !(event.getEntity() instanceof EntityCop)) return;

        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityCop.getNearbyPolice(event.getEntityLiving().getEntityWorld(), event.getSource().getTrueSource().getPosition()).forEach(officer -> {
                if (officer.getEntitySenses().canSee(event.getSource().getTrueSource())) {
                    officer.setChasingPlayer((EntityPlayer) event.getSource().getTrueSource());
                    officer.setKillerPlayer(event.getSource().getTrueSource().getUniqueID());
                }
            });
        }

        event.setCanceled(true);
        if (event.getEntity() instanceof EntityPlayer) {
            ((EntityPlayer) event.getEntity()).setHealth(10);
            ModPolice.setUnconscious((EntityPlayer) event.getEntity(), true, false);
            ModEMT.requestEMT((EntityPlayer) event.getEntity());
        }

        event.getEntity().sendMessage(new TextComponentString(StringHelper.ParseFormatting("&c&lType &6&l/respawn &c&lto respawn. You will lose everything.", '&')));
    }

    @SubscribeEvent
    public void onDamage(LivingDamageEvent event) {
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) return;

        if (!(event.getEntity() instanceof EntityPlayer) && !(event.getEntity() instanceof EntityCop)) return;

        // TODO: Make cop chase player down and then taze
        EntityCop.getNearbyPolice(event.getEntityLiving().getEntityWorld(), event.getSource().getTrueSource().getPosition()).forEach(officer -> {
            if (officer.getEntitySenses().canSee(event.getSource().getTrueSource()) && officer.getDistance(event.getSource().getTrueSource()) < 4) {
                ModPolice.setUnconscious((EntityPlayer) event.getSource().getTrueSource(), true, true);
                event.getSource().getTrueSource().getEntityData().setBoolean("Tazed", false);
                Minelife.getNetwork().sendToAllAround(new PacketPlaySound("minelife:tazer", 1, 1), new NetworkRegistry.TargetPoint(event.getEntity().dimension, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, 10));
            }
        });

    }

    private static void deletePlayer(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if (player.inventory.getStackInSlot(i) != ItemStack.EMPTY) {
                player.entityDropItem(player.inventory.getStackInSlot(i), 0.5f);
            }
        }

        player.inventory.clear();

        File file = new File(System.getProperty("user.dir") + "/world/playerdata", player.getUniqueID().toString() + ".dat");
        if (file != null)
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

            event.player.inventory.clear();
            event.player.inventoryContainer.detectAndSendChanges();

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
        if (PlayerHelper.isOp(event.getPlayer())) return;
        if (Prisoner.isPrisoner(event.getPlayer().getUniqueID()) || ItemHandcuff.isHandcuffed(event.getPlayer()) ||
                (event.getPlayer().isRiding() && (event.getPlayer().getRidingEntity() instanceof EntityCop
                        || ModPolice.isCop(event.getPlayer().getRidingEntity().getUniqueID())))) {
            event.setCanceled(true);
            if (event.getPlayer().isRiding() || ItemHandcuff.isHandcuffed(event.getPlayer())) {
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
            if (nextCheck == 0) {
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
