package com.minelife.capes;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.capes.network.PacketCreateCape;
import com.minelife.capes.network.PacketCreateGui;
import com.minelife.capes.network.PacketUpdateCape;
import com.minelife.capes.network.PacketUpdateCapeStatus;
import com.minelife.capes.server.CommandCape;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

public class ModCapes extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketCreateGui.Handler.class, PacketCreateGui.class, Side.CLIENT);
        registerPacket(PacketCreateCape.Handler.class, PacketCreateCape.class, Side.SERVER);
        registerPacket(PacketUpdateCape.Handler.class, PacketUpdateCape.class, Side.CLIENT);
        registerPacket(PacketUpdateCapeStatus.Handler.class, PacketUpdateCapeStatus.class, Side.CLIENT);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandCape());
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.capes.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.capes.server.ServerProxy.class;
    }
}
