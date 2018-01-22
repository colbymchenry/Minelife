package com.minelife.economy.client;

import com.minelife.MLItems;
import com.minelife.MLProxy;
import com.minelife.MLBlocks;
import com.minelife.economy.TileEntityATM;
import com.minelife.economy.cash.TileEntityCash;
import com.minelife.economy.cash.TileEntityCashRenderer;
import com.minelife.economy.client.wallet.ItemWalletRenderer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityATM.class, new RenderATMBlock());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCash.class, new TileEntityCashRenderer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MLBlocks.atm), new RenderATMItem());
        MinecraftForgeClient.registerItemRenderer(MLItems.wallet, new ItemWalletRenderer());

        MinecraftForge.EVENT_BUS.register(new OnScreenRenderer());
        FMLCommonHandler.instance().bus().register(new OnScreenRenderer());
    }
}
