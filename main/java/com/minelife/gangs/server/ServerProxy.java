package com.minelife.gangs.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.gangs.Gang;
import com.minelife.gangs.ModGangs;
import com.minelife.util.MLConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        File gangDir = new File(Minelife.getConfigDirectory(), "gangs");
        if(gangDir.exists()) {
            for (File file : gangDir.listFiles()) ModGangs.cache_gangs.add(new Gang(file.getName().replaceAll(".yml", "")));
        }
    }
}
