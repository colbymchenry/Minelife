package com.minelife.jobs.client;

import com.minelife.MLProxy;
import com.minelife.jobs.EntityJobNPC;
import com.minelife.jobs.RenderEntityJobNPC;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import hats.common.core.CommonProxy;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        RenderingRegistry.registerEntityRenderingHandler(EntityJobNPC.class, new RenderEntityJobNPC());
    }

}
