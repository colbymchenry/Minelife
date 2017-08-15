package com.minelife.realestate;

import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import com.minelife.realestate.client.packet.BlockPriceRequest;
import com.minelife.realestate.server.packet.BlockPriceResult;
import com.minelife.util.MLConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModRealEstate extends AbstractMod {

    @SideOnly(Side.SERVER)
    public static MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(BlockPriceRequest.Handler.class, BlockPriceRequest.class, Side.SERVER);
        registerPacket(BlockPriceResult.Handler.class, BlockPriceResult.class, Side.CLIENT);
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