package com.minelife.drug.item;

import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemCocaLeafShredded extends Item {

    private static ItemCocaLeafShredded instance;

    private ItemCocaLeafShredded()
    {
        setCreativeTab(CreativeTabs.tabFood);
        setTextureName(Minelife.MOD_ID + ":coca_leaf_shredded");
    }

    public static ItemCocaLeafShredded instance()
    {
        if (instance == null) instance = new ItemCocaLeafShredded();
        return instance;
    }

}
