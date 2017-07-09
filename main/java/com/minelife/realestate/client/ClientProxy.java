package com.minelife.realestate.client;

import com.minelife.CommonProxy;
import com.minelife.realestate.SelectionController;
import com.minelife.realestate.ZoneInfoController;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new SelectionController());
        MinecraftForge.EVENT_BUS.register(new ZoneInfoController.ZoneRenderer());
        FMLCommonHandler.instance().bus().register(new ZoneInfoController.KeyListener());
    }
}
