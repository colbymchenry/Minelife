package com.minelife.region;

import com.minelife.CommonProxy;
import com.minelife.SubMod;
import com.minelife.region.server.CommandRegion;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class ModRegion extends SubMod {

    @Override
    public Class<? extends CommonProxy> getServerProxy() {
        return com.minelife.region.server.ServerProxy.class;
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandRegion());
    }
}
