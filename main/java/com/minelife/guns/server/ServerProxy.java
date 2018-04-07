package com.minelife.guns.server;

import com.minelife.MLProxy;
import com.minelife.guns.Bullet;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ListIterator;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        ListIterator<Bullet> bulletIterator = Bullet.BULLETS.listIterator();
        while(bulletIterator.hasNext()) {
            Bullet.HitResult hitResult = bulletIterator.next().tick(0, false);
            if(hitResult.isTooFar() || hitResult.getBlockState() != null || hitResult.getEntity() != null) bulletIterator.remove();
        }
    }
}
