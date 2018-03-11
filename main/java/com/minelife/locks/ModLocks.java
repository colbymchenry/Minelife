package com.minelife.locks;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModLocks extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityLock.class, "tileEntityLock");
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.locks.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.locks.server.ServerProxy.class;
    }
}
