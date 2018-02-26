package com.minelife.drug.client;

import com.minelife.MLProxy;
import com.minelife.MLBlocks;
import com.minelife.MLItems;
import com.minelife.drug.client.render.*;
import com.minelife.drug.tileentity.TileEntityCementMixer;
import com.minelife.drug.tileentity.TileEntityDryingRack;
import com.minelife.drug.tileentity.TileEntityLeafMulcher;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLeafMulcher.class, new TileEntityLeafMulcherRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDryingRack.class, new TileEntityDryingRackRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCementMixer.class, new TileEntityCementMixerRenderer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MLBlocks.leaf_mulcher), new ItemLeafMulcherRenderer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MLBlocks.cement_mixer), new ItemCementMixerRenderer());
        MinecraftForgeClient.registerItemRenderer(MLItems.coca_leaf, new ItemCocaLeafRenderer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MLBlocks.drying_rack), new ItemDryingRackRenderer());
        MinecraftForgeClient.registerItemRenderer(MLItems.grinder, new ItemGrinderRenderer());
    }
}
