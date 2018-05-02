package com.minelife.minereset;

import com.minelife.MLProxy;
import com.minelife.util.MLConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.UUID;

public class ServerProxy extends MLProxy {

    public static MLConfig config;

    public static Mine mine;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        super.preInit(event);
        config = new MLConfig("mines");
        mine = new Mine(UUID.fromString(config.getString("estateID", null)), config.getInt("duration", 0));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        mine.generate();
    }

}
