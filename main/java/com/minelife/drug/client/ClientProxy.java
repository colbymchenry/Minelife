package com.minelife.drug.client;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import com.minelife.drug.block.BlockLeafMulcher;
import com.minelife.drug.client.render.ItemCocaLeafRenderer;
import com.minelife.drug.client.render.ItemLeafMulcherRenderer;
import com.minelife.drug.client.render.TileEntityDryingRackRenderer;
import com.minelife.drug.client.render.TileEntityLeafMulcherRenderer;
import com.minelife.drug.item.ItemCocaLeaf;
import com.minelife.drug.tileentity.TileEntityDryingRack;
import com.minelife.drug.tileentity.TileEntityEntityLeafMulcher;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEntityLeafMulcher.class, new TileEntityLeafMulcherRenderer());
//        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDryingRack.class, new TileEntityDryingRackRenderer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Minelife.blocks.leaf_mulcher), new ItemLeafMulcherRenderer());
        MinecraftForgeClient.registerItemRenderer(Minelife.items.coca_leaf, new ItemCocaLeafRenderer());
    }
}
