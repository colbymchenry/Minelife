package com.minelife.minebay;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.minebay.network.PacketGetItemListings;
import com.minelife.minebay.network.PacketSendItemListings;
import com.minelife.minebay.server.ServerProxy;
import lib.PatPeter.SQLibrary.Database;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ModMinebay extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketGetItemListings.Handler.class, PacketGetItemListings.class, Side.SERVER);
        registerPacket(PacketSendItemListings.Handler.class, PacketSendItemListings.class, Side.CLIENT);
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.minebay.server.ServerProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.minebay.client.ClientProxy.class;
    }

    public static Database getDatabase() {
        return ServerProxy.DB;
    }

}
