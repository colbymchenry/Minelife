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

        // leaving estate
        if (estate == null && insideEstate.containsKey(player)) {
            estate = insideEstate.get(player);
            insideEstate.remove(player);
            if (!estate.getOutro().trim().isEmpty())
                player.addChatComponentMessage(new ChatComponentText(estate.getOutro()));

            // TODO: Teleportation back in. Also don't allow players to use this permission
//            if (!estate.getPlayerPermissions(player).contains(Permission.EXIT)) {
//            }
            return;
        }

        // entering estate
        if (estate != null) {
            if (!insideEstate.containsKey(player)) {
                insideEstate.put(player, estate);
                if (!estate.getIntro().trim().isEmpty())
                    player.addChatComponentMessage(new ChatComponentText(estate.getIntro()));

                // TODO: Teleporation back out. Also don't allow players to use this permission
//                if (!estate.getPlayerPermissions(player).contains(Permission.ENTER)) {
//                }
            } else {
                if (!insideEstate.get(player).equals(estate)) {
                    insideEstate.put(player, estate);
                    if (!estate.getIntro().trim().isEmpty())
                        player.addChatComponentMessage(new ChatComponentText(estate.getIntro()));
                }
            }
            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBreak(BlockEvent.BreakEvent event) {
        System.out.println("EH");
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
                if(e.getBill() != null) {
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
