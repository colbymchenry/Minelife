package com.minelife.drug.client;

import com.minelife.CommonProxy;
import com.minelife.drug.block.BlockLeafMulcher;
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
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockLeafMulcher.instance()), new ItemLeafMulcherRenderer());
    }
}
