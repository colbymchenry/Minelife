package com.minelife;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.relauncher.Side;

public class SubMod {

    public void preInit(FMLPreInitializationEvent event) {}

    public void init(FMLInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}

    public Class<? extends CommonProxy> getClientProxy() {return null;}

    public Class<? extends CommonProxy> getServerProxy() {return null;}

    private static int PACKET_ID = 0;

    public static final void registerPacket(Class messageHandler, Class message, Side receivingSide) {
        Minelife.NETWORK.registerMessage(messageHandler, message, PACKET_ID++, receivingSide);
    }


}
