package com.minelife.capes.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.capes.network.PacketUpdateCapeStatus;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onEntityTrack(PlayerEvent.StartTracking event) {
        if(!(event.target instanceof EntityPlayer)) return;
        boolean on = event.target.getEntityData().hasKey("cape") ? event.target.getEntityData().getBoolean("cape") : false;
        Minelife.NETWORK.sendTo(new PacketUpdateCapeStatus(event.target.getEntityId(), on), (EntityPlayerMP) event.entityPlayer);
    }

    @SubscribeEvent
    public void onJoin(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        boolean on = event.player.getEntityData().hasKey("cape") ? event.player.getEntityData().getBoolean("cape") : false;
        Minelife.NETWORK.sendTo(new PacketUpdateCapeStatus(event.player.getEntityId(), on), (EntityPlayerMP) event.player);
    }
}
