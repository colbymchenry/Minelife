package com.minelife.notifications;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.notifications.server.ServerProxy;
import lib.PatPeter.SQLibrary.Database;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ModNotifications extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketNotification.Handler.class, PacketNotification.class, Side.CLIENT);
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.notifications.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.notifications.server.ServerProxy.class;
    }

    public static Database getDatabase() {
        return ServerProxy.DB;
    }
}
