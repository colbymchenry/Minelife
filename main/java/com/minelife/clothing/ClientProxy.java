package com.minelife.clothing;

import com.minelife.MLProxy;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        RenderingRegistry.registerEntityRenderingHandler(EntityFakePlayer.class,
                new RenderPlayer());
    }
}
