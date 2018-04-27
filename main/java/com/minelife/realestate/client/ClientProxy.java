package com.minelife.realestate.client;

import com.minelife.MLProxy;
import com.minelife.jobs.EntityJobNPC;
import com.minelife.jobs.client.RenderEntityJobNPC;
import com.minelife.realestate.EntityReceptionist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        RenderingRegistry.registerEntityRenderingHandler(EntityReceptionist.class, RenderEntityReceptionist::new);
        MinecraftForge.EVENT_BUS.register(new SelectionRenderer());
    }
}
