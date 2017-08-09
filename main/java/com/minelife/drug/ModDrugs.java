package com.minelife.drug;

import buildcraft.BuildCraftEnergy;
import com.minelife.*;
import com.minelife.drug.block.*;
import com.minelife.drug.item.*;
import com.minelife.drug.tileentity.TileEntityCementMixer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ModDrugs extends AbstractMod {

    public static final CreativeTabs tab_drugs = new CreativeTabs("tab_drugs") {
        @Override
        public Item getTabIconItem() {
            return MLItems.grinder;
        }
    };

    // TODO: Make one class for all these items, don't need so many classes
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        BucketHandler.INSTANCE.buckets.put(MLBlocks.ammonia, MLItems.ammonia);
        BucketHandler.INSTANCE.buckets.put(MLBlocks.sulfuric_acid, MLItems.sulfuric_acid);
        BucketHandler.INSTANCE.buckets.put(MLBlocks.potassium_permanganate, MLItems.potassium_permanganate);

        // register recipes
        GameRegistry.addShapelessRecipe(new ItemStack(MLItems.calcium_hydroxide), Items.water_bucket, MLItems.calcium_oxide);
        GameRegistry.addSmelting(Item.getItemFromBlock(MLBlocks.limestone), new ItemStack(MLItems.calcium_oxide), 0.3F);
        GameRegistry.addShapelessRecipe(new ItemStack(MLItems.cannabis_shredded), MLItems.cannabis_buds, MLItems.grinder);
        GameRegistry.addShapelessRecipe(new ItemStack(MLItems.coca_leaf_shredded), MLItems.grinder, new ItemStack(MLItems.coca_leaf, 1, 0));
        // register grinder recipes
        GameRegistry.addShapedRecipe(new ItemStack(MLItems.grinder), "AAA", "BBB", "AAA", 'A', Item.getItemFromBlock(Blocks.planks), 'B', Items.iron_ingot);
        for(int i = 0; i < ItemDye.field_150921_b.length; i++) GameRegistry.addShapelessRecipe(new ItemStack(MLItems.grinder, 1, i), MLItems.grinder, new ItemStack(Items.dye, 1, i));
        // continue with other recipes
        GameRegistry.addShapelessRecipe(new ItemStack(MLItems.potassium_hydroxide), Item.getItemFromBlock(MLBlocks.potash), MLItems.calcium_hydroxide);
        GameRegistry.addShapelessRecipe(new ItemStack(MLItems.potassium_permanganate), MLItems.sulfuric_acid, MLItems.potassium_manganate);
        GameRegistry.addSmelting(Items.water_bucket, new ItemStack(MLItems.salt), 1F);
        GameRegistry.addSmelting(MLItems.sulfur, new ItemStack(MLItems.sulfuric_acid), 0.3F);
        GameRegistry.addSmelting(MLItems.potassium_hydroxide_pyrolusite_mixture, new ItemStack(MLItems.potassium_manganate), 0.3F);
        GameRegistry.addSmelting(MLItems.potassium_hydroxide_pyrolusite_mixture, new ItemStack(MLItems.potassium_manganate), 0.3F);
        GameRegistry.addSmelting(MLItems.waxy_cocaine, new ItemStack(MLItems.heated_cocaine), 0.3F);
        GameRegistry.addShapelessRecipe(new ItemStack(MLItems.potassium_hydroxide_pyrolusite_mixture), MLItems.potassium_hydroxide, MLItems.pyrolusite);

        // TODO: Add recipes for manufacturing blocks
        TileEntityCementMixer.add_recipe(Recipe.build(new ItemStack(MLItems.waxy_cocaine), new ItemStack(MLItems.lime), new ItemStack(MLItems.salt)).addLiquids(new FluidStack(BuildCraftEnergy.fluidFuel, 100)));
        TileEntityCementMixer.add_recipe(Recipe.build(new ItemStack(MLItems.cocaine_sulfate), new ItemStack(MLItems.pressed_cocaine)).addLiquids(new FluidStack(MLBlocks.sulfuric_acid.getFluid(), 100)));
        TileEntityCementMixer.add_recipe(Recipe.build(new ItemStack(MLItems.purple_cocaine), new ItemStack(MLItems.coca_paste)).addLiquids(new FluidStack(MLBlocks.potassium_permanganate.getFluid(), 100)));
        TileEntityCementMixer.add_recipe(Recipe.build(new ItemStack(MLItems.processed_cocaine), new ItemStack(MLItems.coca_paste)).addLiquids(new FluidStack(MLBlocks.ammonia.getFluid(), 100)));
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("ammonia", 1000), new ItemStack(MLItems.ammonia), new ItemStack(Items.bucket));
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("potassium_permanganate", 1000), new ItemStack(MLItems.potassium_permanganate), new ItemStack(Items.bucket));
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("sulfuric_acid", 1000), new ItemStack(MLItems.sulfuric_acid), new ItemStack(Items.bucket));
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
            BlockAmmonia.fluid().setIcons(MLBlocks.ammonia.getBlockTextureFromSide(1), MLBlocks.ammonia.getBlockTextureFromSide(2));
            BlockSulfuricAcid.fluid().setIcons(MLBlocks.sulfuric_acid.getBlockTextureFromSide(1), MLBlocks.sulfuric_acid.getBlockTextureFromSide(2));
            BlockPotassiumPermanganate.fluid().setIcons(MLBlocks.potassium_permanganate.getBlockTextureFromSide(1), MLBlocks.potassium_permanganate.getBlockTextureFromSide(2));
        }
    }
}
