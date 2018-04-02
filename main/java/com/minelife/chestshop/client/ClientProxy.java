package com.minelife.chestshop.client;

import codechicken.chunkloader.client.TileChunkLoaderRenderer;
import codechicken.chunkloader.tile.TileChunkLoader;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.render.item.CCRenderItem;
import com.minelife.MLProxy;
import com.minelife.chestshop.ModChestShop;
import com.minelife.chestshop.TileEntityChestShop;
import com.minelife.chestshop.client.render.RenderChestShopBlock;
import com.minelife.chestshop.client.render.RenderChestShopItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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
