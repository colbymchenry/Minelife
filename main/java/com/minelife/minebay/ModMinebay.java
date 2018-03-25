package com.minelife.minebay;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.minebay.network.*;
import com.minelife.minebay.server.ServerProxy;
import lib.PatPeter.SQLibrary.Database;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ModMinebay extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketGetItemListings.Handler.class, PacketGetItemListings.class, Side.SERVER);
        registerPacket(PacketSendItemListings.Handler.class, PacketSendItemListings.class, Side.CLIENT);
        registerPacket(PacketSellItem.Handler.class, PacketSellItem.class, Side.SERVER);
        registerPacket(PacketBuyItem.Handler.class, PacketBuyItem.class, Side.SERVER);
        registerPacket(PacketGetPlayerListings.Handler.class, PacketGetPlayerListings.class, Side.SERVER);
        registerPacket(PacketDeleteListing.Handler.class, PacketDeleteListing.class, Side.SERVER);
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
