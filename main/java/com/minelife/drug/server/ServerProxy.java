package com.minelife.drug.server;

import com.minelife.CommonProxy;
import com.minelife.drug.BucketHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);
    }
}
