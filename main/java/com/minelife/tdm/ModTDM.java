package com.minelife.tdm;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.tdm.network.PacketOpenMatchSearch;
import com.minelife.tdm.server.CommandTDM;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ModTDM extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketOpenMatchSearch.Handler.class, PacketOpenMatchSearch.class, Side.CLIENT);
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.tdm.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.tdm.server.ServerProxy.class;
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandTDM());
    }
}
