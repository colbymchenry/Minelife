package com.minelife.realestate.server;

import com.google.common.collect.Maps;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.realestate.Permission;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.Map;

public class PlayerListener {

    private static Map<EntityPlayer, Estate> insideEstate = Maps.newHashMap();

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        Estate estate = EstateHandler.getEstateAt(player.worldObj, Vec3.createVectorHelper(player.posX, player.posY, player.posZ));

        // leaving estate
        if (estate == null && insideEstate.containsKey(player)) {
            insideEstate.remove(player);
            if (!estate.getOutro().trim().isEmpty())
                player.addChatComponentMessage(new ChatComponentText(estate.getOutro()));

            // TODO: Teleportation back in. Also don't allow players to use this permission
            if(!estate.getPlayerPermissions(player).contains(Permission.EXIT)) {
            }
            return;
        }

        // entering estate
        if (estate != null && !insideEstate.containsKey(player)) {
            insideEstate.put(player, estate);
            if (!estate.getIntro().trim().isEmpty())
                player.addChatComponentMessage(new ChatComponentText(estate.getIntro()));

            // TODO: Teleporation back out. Also don't allow players to use this permission
            if(!estate.getPlayerPermissions(player).contains(Permission.ENTER)) {
            }
            return;
        }

        // still inside estate
        if (insideEstate.containsKey(player) && insideEstate.get(player).equals(estate)) return;
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
        Estate estate = EstateHandler.getEstateAt(player.worldObj, Vec3.createVectorHelper(player.posX, player.posY, player.posZ));
    }

    @SubscribeEvent
    public void onPlace(BlockEvent.PlaceEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        Estate estate = EstateHandler.getEstateAt(player.worldObj, Vec3.createVectorHelper(player.posX, player.posY, player.posZ));
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
        Estate estate = EstateHandler.getEstateAt(player.worldObj, Vec3.createVectorHelper(player.posX, player.posY, player.posZ));
    }

    @SubscribeEvent
    public void onSpawn(EntityJoinWorldEvent event) {
        Estate estate = EstateHandler.getEstateAt(event.world, Vec3.createVectorHelper(event.entity.posX, event.entity.posY, event.entity.posZ));
    }

    @SubscribeEvent
    public void onDamage(LivingHurtEvent event) {
        Estate estate = EstateHandler.getEstateAt(event.entity.worldObj, Vec3.createVectorHelper(event.entity.posX, event.entity.posY, event.entity.posZ));
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent event) {
        Estate estate = EstateHandler.getEstateAt(event.entity.worldObj, Vec3.createVectorHelper(event.entity.posX, event.entity.posY, event.entity.posZ));
    }

    @SubscribeEvent
    public void onHeal(LivingHealEvent event) {
        Estate estate = EstateHandler.getEstateAt(event.entity.worldObj, Vec3.createVectorHelper(event.entity.posX, event.entity.posY, event.entity.posZ));
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        Estate estate = EstateHandler.getEstateAt(event.entity.worldObj, Vec3.createVectorHelper(event.entity.posX, event.entity.posY, event.entity.posZ));
    }

}
