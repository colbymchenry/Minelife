package com.minelife.drug.item;

import com.minelife.Minelife;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemCannabisShredded extends Item {

    private static ItemCannabisShredded instance;

    private ItemCannabisShredded() {
        setCreativeTab(CreativeTabs.tabFood);
        setTextureName(Minelife.MOD_ID + ":shredded_cannabis");
        setUnlocalizedName("cannabis_shredded");
    }

    public static ItemCannabisShredded instance() {
        if(instance == null) instance = new ItemCannabisShredded();
        return instance;
    }

    public static void register_recipe() {
        GameRegistry.addShapelessRecipe(new ItemStack(instance()), "A", "B", 'A', ItemCannabisBuds.instance(), 'B', ItemGrinder.instance());
    }

}
