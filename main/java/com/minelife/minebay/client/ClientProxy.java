package com.minelife.minebay.client;

import com.minelife.MLProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(new KeyListener());
    }
}
