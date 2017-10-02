package com.minelife.realestate;

import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.realestate.client.ClientProxy;
import com.minelife.realestate.server.PacketSendSelection;
import com.minelife.realestate.server.ServerProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

public class ModRegion extends AbstractMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketSendSelection.Handler.class, PacketSendSelection.class, Side.CLIENT);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        super.serverStarting(event);
    }

    @Override
    public Class<? extends CommonProxy> getClientProxyClass() {
        return com.minelife.realestate.client.ClientProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getServerProxyClass() {
        return com.minelife.realestate.server.ServerProxy.class;
    }

    public static ServerProxy getServerProxy() {
        return (ServerProxy) Minelife.getModInstance(ModRegion.class).serverProxy;
    }

    public static ClientProxy getClientProxy() {
        return (ClientProxy) Minelife.getModInstance(ModRegion.class).clientProxy;
    }

}
