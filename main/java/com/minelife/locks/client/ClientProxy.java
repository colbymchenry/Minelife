package com.minelife.locks.client;

import com.minelife.MLProxy;
import com.minelife.locks.TileEntityLock;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLock.class, new TileEntityLockRenderer());
    }
}
