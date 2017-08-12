package com.minelife.realestate.client;

import com.minelife.CommonProxy;
import com.minelife.realestate.client.renderer.ClientListener;
import com.minelife.realestate.client.renderer.ClientRenderer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new ClientListener());
        MinecraftForge.EVENT_BUS.register(new ClientListener());
        MinecraftForge.EVENT_BUS.register(new ClientRenderer());
    }

}