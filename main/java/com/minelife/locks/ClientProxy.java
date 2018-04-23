package com.minelife.locks;

import com.minelife.MLProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        ModLocks.itemLock.registerModels();
        ModLocks.itemLockpick.registerModels();
    }

    @Override
    public void init(FMLInitializationEvent event) throws Exception {

    }
}
