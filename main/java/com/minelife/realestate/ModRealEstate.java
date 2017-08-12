package com.minelife.realestate;

import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.util.SimpleConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.File;

public class ModRealEstate extends AbstractMod {

    @SideOnly(Side.SERVER)
    public static SimpleConfig config;

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