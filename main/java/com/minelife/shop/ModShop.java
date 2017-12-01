package com.minelife.shop;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;

public class ModShop extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityShopBlock.class, "tileShopBlock");
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
