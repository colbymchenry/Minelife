package com.minelife.realestate.server;

import com.google.common.collect.Maps;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.realestate.Permission;
import com.minelife.realestate.RentDueNotification;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EstateListener {

    private static Map<EntityPlayer, Estate> insideEstate = Maps.newHashMap();

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        Estate estate = EstateHandler.getEstateAt(player.worldObj, Vec3.createVectorHelper(player.posX, player.posY, player.posZ));

        if (estate != null && insideEstate.containsKey(player) && !insideEstate.get(player).equals(estate)) {
            if (!insideEstate.get(player).getOutro().trim().isEmpty())
                player.addChatComponentMessage(new ChatComponentText(insideEstate.get(player).getOutro().replaceAll("&", String.valueOf('\u00a7'))));
            if (!estate.getIntro().trim().isEmpty())
                player.addChatComponentMessage(new ChatComponentText(estate.getIntro().replaceAll("&", String.valueOf('\u00a7'))));
        }

        // leaving estate
        if (estate == null && insideEstate.containsKey(player)) {
            System.out.println("BOOM");
            estate = insideEstate.get(player);
            insideEstate.remove(player);
            if (!estate.getOutro().trim().isEmpty())
                player.addChatComponentMessage(new ChatComponentText(estate.getOutro().replaceAll("&", String.valueOf('\u00a7'))));

            // teleport back in, maybe want to add it maybe don't
//            if (!estate.getPlayerPermissions(player).contains(Permission.EXIT)) {
//            }
            return;
        }

        // entering estate
        if (estate != null) {
            if (!insideEstate.containsKey(player)) {
                insideEstate.put(player, estate);
                if (!estate.getIntro().trim().isEmpty())
                    player.addChatComponentMessage(new ChatComponentText(estate.getIntro().replaceAll("&", String.valueOf('\u00a7'))));

                if (!estate.getPlayerPermissions(player).contains(Permission.ENTER)) {
                    double distanceMinX = player.posX - estate.getBounds().minX;
                    double distanceMaxX = estate.getBounds().maxX - player.posX;
                    double distanceMinZ = player.posZ - estate.getBounds().minZ;
                    double distanceMaxZ = estate.getBounds().maxZ - player.posZ;

                    if (player.posX - 1 > estate.getBounds().minX && player.posX + 1 < estate.getBounds().maxX) {
                        if (distanceMinZ < distanceMaxZ) {
                            player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, estate.getBounds().minZ - 0.5, player.rotationYaw, player.rotationPitch);
                        } else if (distanceMinZ > distanceMaxZ) {
                            player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, estate.getBounds().maxZ + 0.5, player.rotationYaw, player.rotationPitch);
                        }
                    } else if (player.posZ > estate.getBounds().minZ && player.posZ < estate.getBounds().maxZ) {
                        if (distanceMinX < distanceMaxX) {
                            player.playerNetServerHandler.setPlayerLocation(estate.getBounds().minX - 0.5, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                        } else if (distanceMinX > distanceMaxX) {
                            player.playerNetServerHandler.setPlayerLocation(estate.getBounds().maxX + 0.5, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                        }
                    }
                }
            } else {
                if (!insideEstate.get(player).equals(estate)) {
                    insideEstate.put(player, estate);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBreak(BlockEvent.BreakEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
        Estate estate = EstateHandler.getEstateAt(player.worldObj, Vec3.createVectorHelper(event.x, event.y, event.z));
        if (estate == null) return;
        event.setCanceled(!estate.getPlayerPermissions(player).contains(Permission.BREAK));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlace(BlockEvent.PlaceEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        Estate estate = EstateHandler.getEstateAt(player.worldObj, Vec3.createVectorHelper(event.x, event.y, event.z));

        System.out.println("CALLED1");
        if (estate == null) return;
        System.out.println("CALLED");
        event.setCanceled(!estate.getPlayerPermissions(player).contains(Permission.PLACE));
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        // TODO:
//        EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
//        Estate estate = EstateHandler.getEstateAt(player.worldObj, Vec3.createVectorHelper(event.x, event.y, event.z));
//        if (estate == null) return;
    }

    @SubscribeEvent
    public void onSpawn(EntityJoinWorldEvent event) {
        Estate estate = EstateHandler.getEstateAt(event.world, Vec3.createVectorHelper(event.entity.posX, event.entity.posY, event.entity.posZ));
        if (estate == null) return;
        if (event.entity instanceof EntityMob || event.entity instanceof IMob)
            event.setCanceled(!estate.getEstatePermissions().contains(Permission.MONSTER_SPAWN));
        else if (event.entity instanceof EntityAnimal)
            event.setCanceled(!estate.getEstatePermissions().contains(Permission.CREATURE_SPAWN));
    }


    @SubscribeEvent
    public void onDamage(LivingHurtEvent event) {
        Estate estate = EstateHandler.getEstateAt(event.entity.worldObj, Vec3.createVectorHelper(event.entity.posX, event.entity.posY, event.entity.posZ));
        if (estate == null) return;
        if (event.entity instanceof EntityPlayer)
            event.setCanceled(!estate.getEstatePermissions().contains(Permission.PVP));
        else
            event.setCanceled(!estate.getEstatePermissions().contains(Permission.PVE));
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent event) {
        Estate estate = EstateHandler.getEstateAt(event.entity.worldObj, Vec3.createVectorHelper(event.entity.posX, event.entity.posY, event.entity.posZ));
        if (estate == null) return;
        event.setCanceled(!estate.getEstatePermissions().contains(Permission.FALL_DAMAGE));
    }

    @SubscribeEvent
    public void onHeal(LivingHealEvent event) {
        Estate estate = EstateHandler.getEstateAt(event.entity.worldObj, Vec3.createVectorHelper(event.entity.posX, event.entity.posY, event.entity.posZ));
        if (estate == null) return;
        event.setCanceled(!estate.getEstatePermissions().contains(Permission.HEAL));
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        Estate estate = EstateHandler.getEstateAt(event.entity.worldObj, Vec3.createVectorHelper(event.entity.posX, event.entity.posY, event.entity.posZ));
        if (estate == null) return;
        if (event.entity instanceof EntityMob)
            event.setCanceled(!estate.getEstatePermissions().contains(Permission.MONSTER_DEATH));
        else if (event.entity instanceof EntityAnimal)
            event.setCanceled(!estate.getEstatePermissions().contains(Permission.CREATURE_DEATH));
        else if (event.entity instanceof EntityPlayer)
            event.setCanceled(!estate.getEstatePermissions().contains(Permission.PLAYER_DEATH));
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EstateHandler.loadedEstates.forEach(e -> {
            if (e.getRenter() != null && e.getRenter().equals(event.player.getUniqueID())) {
                if (e.getBill() != null) {
                    long diff = e.getBill().getDueDate().getTime() - Calendar.getInstance().getTime().getTime();
                    if (e.getBill().getAmountDue() > 0) {
                        RentDueNotification notification = new RentDueNotification(event.player.getUniqueID(), (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS), e.getID(), e.getBill().getAmountDue());
                        notification.sendTo((EntityPlayerMP) event.player);
                    }
                }
            }
        });
    }

}
