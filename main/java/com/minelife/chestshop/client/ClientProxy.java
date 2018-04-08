package com.minelife.chestshop.client;

import com.minelife.MLProxy;
import com.minelife.chestshop.ModChestShop;
import com.minelife.chestshop.TileEntityChestShop;
import com.minelife.chestshop.client.render.RenderChestShopBlock;
import com.minelife.chestshop.client.render.RenderChestShopItem;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerItemRenderer(ModChestShop.itemChestShop, new RenderChestShopItem());
        registerBlockRenderer(TileEntityChestShop.class, new RenderChestShopBlock());
    }

    @Override
    public void init(FMLInitializationEvent event) throws Exception {

    }
}
