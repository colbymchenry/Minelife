package com.minelife.realestate.server;

import com.google.common.collect.Maps;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
        if(estate == null && insideEstate.containsKey(player)) {
            insideEstate.remove(player);
            return;
        }

        // entering estate
        if(estate != null && !insideEstate.containsKey(player)) {
            insideEstate.put(player, estate);
            return;
        }

        // still inside estate
        if(insideEstate.containsKey(player) && insideEstate.get(player).equals(estate)) return;
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {

    }

    @SubscribeEvent
    public void onPlace(BlockEvent.PlaceEvent event) {

    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {

    }

    @SubscribeEvent
    public void onSpawn(EntityJoinWorldEvent event) {

    }

    @SubscribeEvent
    public void onDamage(LivingHurtEvent event) {

    }

    @SubscribeEvent
    public void onFall(LivingFallEvent event) {

    }

    @SubscribeEvent
    public void onHeal(LivingHealEvent event) {

    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {

    }

}
