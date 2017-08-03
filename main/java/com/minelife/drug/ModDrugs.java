package com.minelife.drug;

import com.minelife.AbstractGuiHandler;
import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import com.minelife.drug.block.*;
import com.minelife.drug.item.*;
import com.minelife.drug.tileentity.TileEntityAmmoniaExtractor;
import com.minelife.drug.tileentity.TileEntityDryingRack;
import com.minelife.drug.tileentity.TileEntityEntityLeafMulcher;
import com.minelife.gun.item.ammos.ItemAmmo;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.client.event.TextureStitchEvent;

public class ModDrugs extends AbstractMod {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        // register fluids
        BlockAmmonia.register_fluid();
        BlockSulfuricAcid.register_fluid();

        // register tile entities
        GameRegistry.registerTileEntity(TileEntityEntityLeafMulcher.class, "leaf_mulcher");
        GameRegistry.registerTileEntity(TileEntityAmmoniaExtractor.class, "ammonia_extractor");
//        GameRegistry.registerTileEntity(TileEntityDryingRack.class, "drying_rack");

        // register items
        GameRegistry.registerItem(ItemCocaSeeds.instance(), "coca_seeds");
        GameRegistry.registerItem(ItemCocaLeaf.instance(), "coca_leaf");
        GameRegistry.registerItem(ItemCannabisSeeds.instance(), "cannabis_seeds");
        GameRegistry.registerItem(ItemCannabisBuds.instance(), "cannabis_buds");
        GameRegistry.registerItem(ItemCannabisShredded.instance(), "cannabis_shredded");
        GameRegistry.registerItem(ItemCocaLeafShredded.instance(), "shredded_coca_leaf");
        GameRegistry.registerItem(ItemGrinder.instance(), "grinder");
        GameRegistry.registerItem(ItemSalt.instance(), "salt");
        GameRegistry.registerItem(ItemSulfur.instance(), "sulfur");
        GameRegistry.registerItem(ItemLime.instance(), "lime");
        GameRegistry.registerItem(ItemLimeSeeds.instance(), "lime_seeds");
        GameRegistry.registerItem(ItemAmmonia.instance(), "ammonia_item");
        GameRegistry.registerItem(ItemSulfuricAcid.instance(), "sulfuric_acid_item");

        // register blocks
        GameRegistry.registerBlock(BlockCocaPlant.instance(), "coca_plant");
        GameRegistry.registerBlock(BlockCannabisPlant.instance(), "cannabis_plant");
        GameRegistry.registerBlock(BlockLeafMulcher.instance(), "leaf_mulcher");
        GameRegistry.registerBlock(BlockAmmoniaExtractor.instance(), "ammonia_extractor");
//        GameRegistry.registerBlock(BlockDryingRack.instance(), "drying_rack");
        GameRegistry.registerBlock(BlockAmmonia.instance(), "ammonia");
        GameRegistry.registerBlock(BlockSulfuricAcid.instance(), "sulfuric_acid");

        BucketHandler.INSTANCE.buckets.put(BlockAmmonia.instance(), ItemAmmonia.instance());
        BucketHandler.INSTANCE.buckets.put(BlockSulfuricAcid.instance(), ItemSulfuricAcid.instance());

        // register recipes
        ItemCannabisShredded.register_recipe();
        ItemCocaLeafShredded.register_recipe();
        ItemGrinder.register_recipe();
        ItemSalt.register_recipe();
        ItemSulfuricAcid.register_recipe();

        // register world generators
        GameRegistry.registerWorldGenerator(new BlockSulfurOre.Generator(), 0);
    }

    @Override
    public Class<? extends CommonProxy> getClientProxy()
    {
        return com.minelife.drug.client.ClientProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getServerProxy()
    {
        return com.minelife.drug.server.ServerProxy.class;
    }

    @Override
    public AbstractGuiHandler gui_handler()
    {
        return new DrugsGuiHandler();
    }

    @Override
    public void textureHook(TextureStitchEvent.Post event)
    {
        if (event.map.getTextureType() == 0) {
            BlockAmmonia.fluid().setIcons(BlockAmmonia.instance().getBlockTextureFromSide(1), BlockAmmonia.instance().getBlockTextureFromSide(2));
            BlockSulfuricAcid.fluid().setIcons(BlockSulfuricAcid.instance().getBlockTextureFromSide(1), BlockSulfuricAcid.instance().getBlockTextureFromSide(2));
        }
    }
}
