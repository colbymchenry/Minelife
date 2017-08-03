package com.minelife.drug.item;

import com.minelife.Minelife;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemCocaLeafShredded extends Item {

    private static ItemCocaLeafShredded instance;

    private ItemCocaLeafShredded()
    {
        setCreativeTab(CreativeTabs.tabFood);
        setTextureName(Minelife.MOD_ID + ":coca_leaf_shredded");
        setUnlocalizedName("coca_leaf_shredded");
    }

    public static ItemCocaLeafShredded instance()
    {
        if (instance == null) instance = new ItemCocaLeafShredded();
        return instance;
    }

    public static void register_recipe() {
        GameRegistry.addShapelessRecipe(new ItemStack(instance()), ItemGrinder.instance(), ItemCocaLeaf.instance());
    }

}
