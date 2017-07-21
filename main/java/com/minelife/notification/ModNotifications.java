package com.minelife.notification;

import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

public class ModNotifications extends AbstractMod {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        registerPacket(PacketSendNotification.Handler.class, PacketSendNotification.class, Side.CLIENT);
    }

    @Override
    public Class<? extends CommonProxy> getClientProxy()
    {
        return com.minelife.notification.ClientProxy.class;
    }
}
