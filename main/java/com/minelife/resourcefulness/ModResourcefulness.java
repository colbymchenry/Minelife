package com.minelife.resourcefulness;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.resourcefulness.server.CommandForest;
import com.minelife.resourcefulness.server.CommandQuarry;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ModResourcefulness extends MLMod {

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandQuarry());
        event.registerServerCommand(new CommandForest());
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.resourcefulness.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.resourcefulness.server.ServerProxy.class;
    }
}
