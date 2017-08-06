package com.minelife.drug;

import com.minelife.AbstractGuiHandler;
import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.drug.block.*;
import com.minelife.drug.item.*;
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

public class ModDrugs extends AbstractMod {

    public static final CreativeTabs tab_drugs = new CreativeTabs("tab_drugs") {
        @Override
        public Item getTabIconItem() {
            return Minelife.items.grinder;
        }
    };

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        BucketHandler.INSTANCE.buckets.put(Minelife.blocks.ammonia, Minelife.items.ammonia);
        BucketHandler.INSTANCE.buckets.put(Minelife.blocks.sulfuric_acid, Minelife.items.sulfuric_acid);
        BucketHandler.INSTANCE.buckets.put(Minelife.blocks.potassium_permanganate, Minelife.items.potassium_permanganate);

        // register recipes
        GameRegistry.addShapelessRecipe(new ItemStack(Minelife.items.calcium_hydroxide), Items.water_bucket, Minelife.items.calcium_oxide);
        GameRegistry.addSmelting(Item.getItemFromBlock(Minelife.blocks.limestone), new ItemStack(Minelife.items.calcium_oxide), 0.3F);
        GameRegistry.addShapelessRecipe(new ItemStack(Minelife.items.cannabis_shredded), Minelife.items.cannabis_buds, Minelife.items.grinder);
        GameRegistry.addShapelessRecipe(new ItemStack(Minelife.items.coca_leaf_shredded), Minelife.items.grinder, new ItemStack(Minelife.items.coca_leaf, 1, 0));
        // register grinder recipes
        GameRegistry.addShapedRecipe(new ItemStack(Minelife.items.grinder), "AAA", "BBB", "AAA", 'A', Item.getItemFromBlock(Blocks.planks), 'B', Items.iron_ingot);
        for(int i = 0; i < ItemDye.field_150921_b.length; i++) GameRegistry.addShapelessRecipe(new ItemStack(Minelife.items.grinder, 1, i), Minelife.items.grinder, new ItemStack(Items.dye, 1, i));
        // continue with other recipes
        GameRegistry.addShapelessRecipe(new ItemStack(Minelife.items.potassium_hydroxide), Item.getItemFromBlock(Minelife.blocks.potash), Minelife.items.calcium_hydroxide);
        GameRegistry.addShapelessRecipe(new ItemStack(Minelife.items.potassium_permanganate), Minelife.items.sulfuric_acid, Minelife.items.potassium_manganate);
        GameRegistry.addSmelting(Items.water_bucket, new ItemStack(Minelife.items.salt), 1F);
        GameRegistry.addSmelting(Minelife.items.sulfur, new ItemStack(Minelife.items.sulfuric_acid), 0.3F);
        GameRegistry.addSmelting(Minelife.items.potassium_hydroxide_pyrolusite_mixture, new ItemStack(Minelife.items.potassium_manganate), 0.3F);
        GameRegistry.addShapelessRecipe(new ItemStack(Minelife.items.potassium_hydroxide_pyrolusite_mixture), Minelife.items.potassium_hydroxide, Minelife.items.pyrolusite);
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("ammonia", 1000), new ItemStack(Minelife.items.ammonia), new ItemStack(Items.bucket));
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("potassium_permanganate", 1000), new ItemStack(Minelife.items.potassium_permanganate), new ItemStack(Items.bucket));
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("sulfuric_acid", 1000), new ItemStack(Minelife.items.sulfuric_acid), new ItemStack(Items.bucket));
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
            BlockAmmonia.fluid().setIcons(Minelife.blocks.ammonia.getBlockTextureFromSide(1), Minelife.blocks.ammonia.getBlockTextureFromSide(2));
            BlockSulfuricAcid.fluid().setIcons(Minelife.blocks.sulfuric_acid.getBlockTextureFromSide(1), Minelife.blocks.sulfuric_acid.getBlockTextureFromSide(2));
            BlockPotassiumPermanganate.fluid().setIcons(Minelife.blocks.potassium_permanganate.getBlockTextureFromSide(1), Minelife.blocks.potassium_permanganate.getBlockTextureFromSide(2));
        }
    }
}
