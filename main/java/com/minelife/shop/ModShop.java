package com.minelife.shop;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.shop.network.PacketBuyFromShop;
import com.minelife.shop.network.PacketSetShopBlock;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

public class ModShop extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityShopBlock.class, "tileShopBlock");
        registerPacket(PacketSetShopBlock.Handler.class, PacketSetShopBlock.class, Side.SERVER);
        registerPacket(PacketBuyFromShop.Handler.class, PacketBuyFromShop.class, Side.SERVER);
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.shop.server.ServerProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.shop.client.ClientProxy.class;
    }
}
