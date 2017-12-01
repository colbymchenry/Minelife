package com.minelife.notification;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

public class ModNotifications extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        registerPacket(PacketSendNotification.Handler.class, PacketSendNotification.class, Side.CLIENT);
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass()
    {
        return com.minelife.notification.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass()
    {
        return com.minelife.notification.ServerProxy.class;
    }
}
