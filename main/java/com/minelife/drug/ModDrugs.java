package com.minelife.drug;

import buildcraft.BuildCraftEnergy;
import buildcraft.core.recipes.RefineryRecipeManager;
import com.minelife.AbstractGuiHandler;
import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.drug.block.*;
import com.minelife.drug.item.*;
import com.minelife.drug.tileentity.TileEntityEntityLeafMulcher;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.block.machine.tileentity.TileEntityInduction;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class ModDrugs extends AbstractMod {

    public static final CreativeTabs tab_drugs = new CreativeTabs("tab_drugs") {
        @Override
        public Item getTabIconItem() {
            return ItemGrinder.instance();
        }
    };

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        // register fluids
        BlockAmmonia.register_fluid();
        BlockSulfuricAcid.register_fluid();
        BlockPotassiumPermanganate.register_fluid();

        // register tile entities
        GameRegistry.registerTileEntity(TileEntityEntityLeafMulcher.class, "leaf_mulcher");
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
        GameRegistry.registerItem(ItemPyrolusite.instance(), "pyrolusite");
        GameRegistry.registerItem(ItemPotassiumPermanganate.instance(), "potassium_permanganate");

        // register blocks
        GameRegistry.registerBlock(BlockCocaPlant.instance(), "coca_plant");
        GameRegistry.registerBlock(BlockCannabisPlant.instance(), "cannabis_plant");
        GameRegistry.registerBlock(BlockLeafMulcher.instance(), "leaf_mulcher");
//        GameRegistry.registerBlock(BlockDryingRack.instance(), "drying_rack");
        GameRegistry.registerBlock(BlockAmmonia.instance(), "ammonia");
        GameRegistry.registerBlock(BlockSulfuricAcid.instance(), "sulfuric_acid");
        GameRegistry.registerBlock(BlockPyrolusiteBlock.instance(), "pyrolusite_block");
        GameRegistry.registerBlock(BlockPyrolusiteOre.instance(), "pyrolusite_ore");
        GameRegistry.registerBlock(BlockPotassiumPermanganate.instance(), "potassium_permanganate");
        GameRegistry.registerBlock(BlockLimestone.instance(), "limestone");
        GameRegistry.registerBlock(BlockPotash.instance(), "potash");

        //TODO : Figure out the first step with two things burning for a product. Would suck to have a block just for that
        // pyrolusite + potassium hydroxide + heated in air = potassium manganate
        // potash + calcium hydroxide (slaked lime) = potassium hydroxide
        // calcium oxide + water = calcium hydroxide
        // limestone + heat = calcium oxide
        // potassium manganate + sulfuric acid = potassium permanganate
        // Potash ores are typically obtained by conventional shaft mining with the extracted ore ground into a powder.
        
        BucketHandler.INSTANCE.buckets.put(BlockAmmonia.instance(), ItemAmmonia.instance());
        BucketHandler.INSTANCE.buckets.put(BlockSulfuricAcid.instance(), ItemSulfuricAcid.instance());
        BucketHandler.INSTANCE.buckets.put(BlockPotassiumPermanganate.instance(), ItemPotassiumPermanganate.instance());
        // TODO: Store usage of drugs for drug tests
        // register recipes
        ItemCannabisShredded.register_recipe();
        ItemCocaLeafShredded.register_recipe();
        ItemGrinder.register_recipe();
        ItemSalt.register_recipe();
        ItemSulfuricAcid.register_recipe();
        RefineryRecipeManager.INSTANCE.addRecipe(Minelife.MOD_ID + ":ammonia", new FluidStack(BuildCraftEnergy.fluidFuel, 1), new FluidStack(BlockAmmonia.fluid(), 1), 120, 1);

        // register world generators
        GameRegistry.registerWorldGenerator(new BlockSulfurOre.Generator(), 6);
        GameRegistry.registerWorldGenerator(new BlockPyrolusiteOre.Generator(), 5);
        GameRegistry.registerWorldGenerator(new BlockLimestone.Generator(), 0);
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
