package com.minelife.realestate.client;

import com.minelife.CommonProxy;
import com.minelife.realestate.server.SelectionHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(new SelectionHandler.Client());
    }
}
