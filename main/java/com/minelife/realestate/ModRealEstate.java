package com.minelife.realestate;

import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import com.minelife.realestate.network.PacketOpenEstateGui;
import com.minelife.realestate.network.PacketRequestToOpenEstateCreationForm;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

public class ModRealEstate extends AbstractMod {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        registerPacket(PacketOpenEstateGui.Handler.class, PacketOpenEstateGui.class, Side.CLIENT);
        registerPacket(PacketRequestToOpenEstateCreationForm.Handler.class, PacketRequestToOpenEstateCreationForm.class, Side.SERVER);
    }

    @Override
    public Class<? extends CommonProxy> getClientProxyClass()
    {
        return com.minelife.realestate.client.ClientProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getServerProxyClass()
    {
        return com.minelife.realestate.server.ServerProxy.class;
    }
}
