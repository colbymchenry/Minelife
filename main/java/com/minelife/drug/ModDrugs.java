package com.minelife.drug;

import com.minelife.AbstractMod;
import com.minelife.drug.block.BlockCannabisPlant;
import com.minelife.drug.block.BlockCocaPlant;
import com.minelife.drug.item.*;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModDrugs extends AbstractMod {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        GameRegistry.registerItem(ItemCocaSeeds.instance(), "coca_seeds");
        GameRegistry.registerItem(ItemCocaLeaf.instance(false), "coca_leaf");
        GameRegistry.registerItem(ItemCocaLeaf.instance(true), "coca_leaf_dry");
        GameRegistry.registerItem(ItemCannabisSeeds.instance(), "cannabis_seeds");
        GameRegistry.registerItem(ItemCannabisBuds.instance(), "cannabis_buds");
        GameRegistry.registerItem(ItemCannabisShredded.instance(), "shredded_cannabis");
        GameRegistry.registerBlock(BlockCocaPlant.instance(), "coca_plant");
        GameRegistry.registerBlock(BlockCannabisPlant.instance(), "cannabis_plant");
    }
}
