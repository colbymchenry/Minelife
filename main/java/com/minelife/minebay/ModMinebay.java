package com.minelife.minebay;

import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import com.minelife.minebay.packet.PacketListings;
import com.minelife.minebay.packet.PacketResponseListings;
import com.minelife.minebay.packet.PacketSellItem;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

public class ModMinebay extends AbstractMod {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        registerPacket(PacketListings.Handler.class, PacketListings.class, Side.SERVER);
        registerPacket(PacketResponseListings.Handler.class, PacketResponseListings.class, Side.CLIENT);
        registerPacket(PacketSellItem.Handler.class, PacketSellItem.class, Side.SERVER);
    }

    @Override
    public Class<? extends CommonProxy> getClientProxy()
    {
        return com.minelife.minebay.client.ClientProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getServerProxy()
    {
        return com.minelife.minebay.server.ServerProxy.class;
    }
}
