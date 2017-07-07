package com.minelife.realestate;

import com.minelife.CommonProxy;
import com.minelife.SubMod;
import com.minelife.util.SimpleConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModRealEstate extends SubMod {

    @SideOnly(Side.SERVER)
    public static SimpleConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        GameRegistry.registerItem(SelectionController.Selector.getInstance(), "Selector");
        registerPacket(SelectionController.PacketSelection.Handler.class, SelectionController.PacketSelection.class, Side.CLIENT);
    }

    @Override
    public Class<? extends CommonProxy> getClientProxy()
    {
        return com.minelife.realestate.client.ClientProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getServerProxy()
    {
        return com.minelife.realestate.server.ServerProxy.class;
    }
}
