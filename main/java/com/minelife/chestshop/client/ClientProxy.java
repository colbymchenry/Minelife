package com.minelife.chestshop.client;

import com.minelife.MLProxy;
import com.minelife.chestshop.ModChestShop;
import com.minelife.chestshop.TileEntityChestShop;
import com.minelife.chestshop.client.render.RenderChestShopBlock;
import com.minelife.chestshop.client.render.RenderChestShopItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerItemRenderer(ModChestShop.itemChestShop, 0, "minelife:chest_shop", new RenderChestShopItem());
        registerBlockRenderer(TileEntityChestShop.class, new RenderChestShopBlock());
    }
}
