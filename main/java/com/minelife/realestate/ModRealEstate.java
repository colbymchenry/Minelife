package com.minelife.realestate;

import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import com.minelife.realestate.packets.client.BlockPriceRequestPacket;
import com.minelife.realestate.packets.client.RegionPurchaseRequestPacket;
import com.minelife.realestate.packets.server.BlockPriceResultPacket;
import com.minelife.util.MLConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModRealEstate extends AbstractMod {

    @SideOnly(Side.SERVER)
    public static MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
//        Packet.registerPackets();
        registerPacket(BlockPriceRequestPacket.Handler.class, BlockPriceRequestPacket.class, Side.SERVER);
        registerPacket(BlockPriceResultPacket.Handler.class, BlockPriceResultPacket.class, Side.CLIENT);
        registerPacket(RegionPurchaseRequestPacket.Handler.class, RegionPurchaseRequestPacket.class, Side.SERVER);
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