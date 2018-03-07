package com.minelife.casino.server;

import com.minelife.MLProxy;
import com.minelife.casino.ModCasino;
import com.minelife.util.MLConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        ModCasino.config = new MLConfig("casino");
    }
}
