package com.minelife.jobs.client;

import com.minelife.MLProxy;
import com.minelife.jobs.EntityJobNPC;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityJobNPC.class, RenderEntityJobNPC::new);
    }
}
