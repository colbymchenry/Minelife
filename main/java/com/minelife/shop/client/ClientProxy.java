package com.minelife.shop.client;

import com.minelife.MLBlocks;
import com.minelife.MLProxy;
import com.minelife.shop.TileEntityShopBlock;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityShopBlock.class, new RenderBlockShopBlock());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MLBlocks.shopBlock), new RenderItemShopBlock());
    }
}
