package com.minelife.region;

import com.minelife.CommonProxy;
import com.minelife.SubMod;
import com.minelife.region.client.WorldEditSelectionController;
import com.minelife.region.server.CommandRegion;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

public class ModRegion extends SubMod {

    @Override
    public Class<? extends CommonProxy> getServerProxy() {
        return com.minelife.region.server.ServerProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getClientProxy()
    {
        return com.minelife.region.client.ClientProxy.class;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        registerPacket(WorldEditSelectionController.PacketSelection.Handler.class, WorldEditSelectionController.PacketSelection.class, Side.CLIENT);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandRegion());
    }
}
