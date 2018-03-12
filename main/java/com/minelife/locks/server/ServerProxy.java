package com.minelife.locks.server;

import com.minelife.MLProxy;
import com.minelife.core.event.EntityDismountEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
    }


    @SubscribeEvent
    public void onDismound(EntityDismountEvent event) {
        System.out.println("HEHE");
    }


}
