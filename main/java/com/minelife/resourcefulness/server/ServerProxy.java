package com.minelife.resourcefulness.server;

import com.minelife.MLProxy;
import com.minelife.resourcefulness.forest.ForestListener;
import com.minelife.resourcefulness.quarry.QuarryListener;
import com.minelife.util.MLConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends MLProxy {

    public static MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(new QuarryListener());
        MinecraftForge.EVENT_BUS.register(new ForestListener());
    }
}
