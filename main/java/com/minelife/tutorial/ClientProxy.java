package com.minelife.tutorial;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ClientProxy extends MLProxy {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        new File(Minelife.getConfigDirectory(), "tutorials").mkdirs();
    }
}
