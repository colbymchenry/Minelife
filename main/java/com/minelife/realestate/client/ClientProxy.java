package com.minelife.realestate.client;

import com.minelife.CommonProxy;
import com.minelife.realestate.client.listener.ClientListener;
import com.minelife.realestate.client.renderer.InWorldRenderer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(new InWorldRenderer());
        MinecraftForge.EVENT_BUS.register(new ClientListener());
        FMLCommonHandler.instance().bus().register(new ClientListener());
    }

}