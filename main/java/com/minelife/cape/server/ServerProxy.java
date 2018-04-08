package com.minelife.cape.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.cape.network.PacketUpdateCapeStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityTrack(PlayerEvent.StartTracking event) {
        if(!(event.getTarget() instanceof EntityPlayer)) return;
        boolean on = event.getTarget().getEntityData().hasKey("Cape") ? event.getTarget().getEntityData().getBoolean("Cape") : false;
        Minelife.getNetwork().sendTo(new PacketUpdateCapeStatus(event.getTarget().getEntityId(), on), (EntityPlayerMP) event.getEntityPlayer());
    }

}
