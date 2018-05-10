package com.minelife.emt;

import com.google.common.collect.Lists;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.emt.entity.EntityEMT;
import com.minelife.police.ModPolice;
import com.minelife.util.MLConfig;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.PlayerHelper;
import com.minelife.welfare.WelfarePayoutEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ServerProxy extends MLProxy {

    public static MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        config = new MLConfig("emt");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPayout(WelfarePayoutEvent event) {
        event.setAmount(event.getAmount() + 1024);
    }

    @SubscribeEvent
    public void onHeal(PlayerInteractEvent event) {
        if (!ModEMT.isEMT(event.getEntityPlayer().getUniqueID())) return;

        PlayerHelper.TargetResult targetResult = PlayerHelper.getTarget(event.getEntityPlayer(), 5);
        Entity clickedEntity = targetResult.getEntity();

        if (!(clickedEntity instanceof EntityPlayer)) return;

        EntityPlayer playerClicked = (EntityPlayer) clickedEntity;

        if (!ModPolice.isUnconscious(playerClicked)) return;

        if (ModEMT.PLAYERS_BEING_HEALED.containsKey(playerClicked.getUniqueID())) {
            event.getEntityPlayer().sendMessage(new TextComponentString("That player is already being revived."));
            return;
        }

        ModEMT.PLAYERS_BEING_HEALED.put(playerClicked.getUniqueID(), System.currentTimeMillis() + (1000L * 21));
        Minelife.getNetwork().sendTo(new PacketReviving(), (EntityPlayerMP) event.getEntityPlayer());
        Minelife.getNetwork().sendToAllAround(new PacketPlaySound("minelife:emt_revive", 1, 1, event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ), new NetworkRegistry.TargetPoint(event.getEntityPlayer().dimension, event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ, 10));
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        List<UUID> toRemove = Lists.newArrayList();
        ModEMT.PLAYERS_BEING_HEALED.forEach((playerID, reviveTime) -> {
            if (System.currentTimeMillis() >= reviveTime) {
                toRemove.add(playerID);
                EntityPlayer player = PlayerHelper.getPlayer(playerID);
                if (player != null) {
                    ModPolice.setUnconscious(player, false, false);

                    EntityEMT emtRevivingPlayer = getEMTsForWorld(player.getEntityWorld()).stream().filter(entityEMT -> entityEMT.getAttackTarget() != null && entityEMT.getAttackTarget().getUniqueID().equals(playerID)).findFirst().orElse(null);
                    if(emtRevivingPlayer != null) {
                        emtRevivingPlayer.setRevivingPlayer(null);
                        emtRevivingPlayer.setAttackTarget(null);
                        emtRevivingPlayer.getNavigator().clearPath();
                        BlockPos randomPos = emtRevivingPlayer.getRandomSpawnPoint();
                        emtRevivingPlayer.setPositionAndUpdate(randomPos.getX(), randomPos.getY() + 0.5, randomPos.getZ());
                    }
                }
            }
        });

        toRemove.forEach(playerID -> ModEMT.PLAYERS_BEING_HEALED.remove(playerID));

        if (nextCheck <= System.currentTimeMillis()) {
            // initial setup
            if (nextCheck == 0) {
                cleanEMTs(FMLServerHandler.instance().getServer().worlds[0]);
                spawnEMTs(FMLServerHandler.instance().getServer().worlds[0]);
                nextCheck = System.currentTimeMillis() + 60000L;
                return;
            }

            nextCheck = System.currentTimeMillis() + 60000L;

            List<EntityEMT> EMTs = getEMTsForWorld(FMLServerHandler.instance().getServer().worlds[0]);
            if (EMTs.size() > config.getInt("MaxEMTs", 30)) {
                int toDelete = EMTs.size() - config.getInt("MaxEMTs", 30);
                for (int i = 0; i < toDelete; i++) {
                    deleteRandomEMT(FMLServerHandler.instance().getServer().worlds[0]);
                }
            }
        }
    }

    private static Random random = new Random();
    private static long nextCheck = 0L;

    public static void spawnEMTs(World world) {
        int maxEMTs = config.getInt("MaxEMTs", 30);
        List<String> spawns = config.getStringList("EMTSpawnPoints") != null ? config.getStringList("EMTSpawnPoints") : null;
        int policePerSpawn = spawns.isEmpty() ? 0 : maxEMTs / spawns.size();

        for (String spawn : spawns) {
            String[] data = spawn.split(",");
            BlockPos pos = new BlockPos(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            for (int i = 0; i < policePerSpawn; i++) {
                EntityEMT EMT = new EntityEMT(world);
                EMT.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                world.spawnEntity(EMT);
            }
        }
    }

    public static void cleanEMTs(World world) {
        getEMTsForWorld(world).forEach(Entity::setDead);
    }

    public static EntityEMT deleteRandomEMT(World world) {
        List<EntityEMT> EMTs = getEMTsForWorld(world);
        EntityEMT EMT = EMTs.get(random.nextInt(EMTs.size()));
        EMTs.remove(EMT);
        EMT.setDead();
        return EMT;
    }

    public static List<EntityEMT> getEMTsForWorld(World world) {
        List<EntityEMT> EMTs = Lists.newArrayList();

        for (Entity entity : world.loadedEntityList) {
            if (entity instanceof EntityEMT) {
                EMTs.add((EntityEMT) entity);
            }
        }

        return EMTs;
    }
}
