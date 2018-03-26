package com.minelife.guns.server;

import com.minelife.MLProxy;
import com.minelife.guns.Bullet;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        Bullet.BULLETS.removeIf(bullet -> bullet.tick(0));
    }
}
