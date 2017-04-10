package com.minelife.police.server;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Map;

public class TickHandler {

    public static Map<EntityPlayer, EntityPlayer> CUFFED = Maps.newHashMap();

    //Called whenever the player is updated or ticked.
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    }

//    //Called when the client ticks.
//    @SubscribeEvent
//    public void onClientTick(TickEvent.ClientTickEvent event) {
//
//    }

    //Called when the server ticks. Usually 20 ticks a second.
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {

    }

//    //Called when a new frame is displayed (See fps)
//    @SubscribeEvent
//    public void onRenderTick(TickEvent.RenderTickEvent event) {
//
//    }
//
//    //Called when the world ticks
//    @SubscribeEvent
//    public void onWorldTick(TickEvent.WorldTickEvent event) {
//
//    }

}
