package com.minelife.tdm.server;

import com.minelife.MLProxy;
import com.minelife.tdm.Arena;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        Arena.initArenas();
    }
}
