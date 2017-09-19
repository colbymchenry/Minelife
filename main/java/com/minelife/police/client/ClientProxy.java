package com.minelife.police.client;

import com.minelife.CommonProxy;
import com.minelife.MLBlocks;
import com.minelife.police.computer.ItemPoliceComputerRenderer;
import com.minelife.police.computer.TileEntityPoliceComputer;
import com.minelife.police.computer.TileEntityPoliceComputerRenderer;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

// TODO: Render on screen if in prison the time left to serve and the bail that they owe
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPoliceComputer.class, new TileEntityPoliceComputerRenderer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MLBlocks.policeComputer), new ItemPoliceComputerRenderer());
    }
}
