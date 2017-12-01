package com.minelife.economy.client;

import com.minelife.MLProxy;
import com.minelife.MLBlocks;
import com.minelife.economy.TileEntityATM;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityATM.class, new RenderATMBlock());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MLBlocks.atm), new RenderATMItem());

        MinecraftForge.EVENT_BUS.register(new OnScreenRenderer());
    }
}
