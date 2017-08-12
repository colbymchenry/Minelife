package com.minelife.realestate;

import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ModRealEstate extends AbstractMod {

    public static int pricePerBlock = 2;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        Packet.registerPackets();
    }

    @Override
    public Class<? extends CommonProxy> getClientProxy() {
        return com.minelife.realestate.client.ClientProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getServerProxy() {
        return com.minelife.realestate.server.ServerProxy.class;
    }

}