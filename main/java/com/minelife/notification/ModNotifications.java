package com.minelife.notification;

import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import com.minelife.Minelife;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

import java.sql.SQLException;

public class ModNotifications extends AbstractMod {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        registerPacket(PacketSendNotification.Handler.class, PacketSendNotification.class, Side.CLIENT);
    }

    @Override
    public Class<? extends CommonProxy> getClientProxyClass()
    {
        return com.minelife.notification.ClientProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getServerProxyClass()
    {
        return com.minelife.notification.ServerProxy.class;
    }
}
