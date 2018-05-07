package com.minelife.police;

import blusunrize.immersiveengineering.common.IEContent;
import com.minelife.Minelife;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemHandcuff extends Item {

    public ItemHandcuff() {
        setRegistryName(Minelife.MOD_ID, "handcuff");
        setUnlocalizedName(Minelife.MOD_ID + ":handcuff");
        setCreativeTab(CreativeTabs.MISC);
    }

    public void registerModel() {
        ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Minelife.MOD_ID + ":handcuff", "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, itemModelResourceLocation);
    }

    public void registerRecipe() {
        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":handcuff");
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this),
                "AA",
                "AA",
                'A', Ingredient.fromStacks(new ItemStack(Items.IRON_INGOT)));
    }

}
