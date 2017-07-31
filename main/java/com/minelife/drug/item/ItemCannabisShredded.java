package com.minelife.drug.item;

import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemCannabisShredded extends Item {

    private static ItemCannabisShredded instance;

    private ItemCannabisShredded() {
        setCreativeTab(CreativeTabs.tabFood);
        setTextureName(Minelife.MOD_ID + ":shredded_cannabis");
    }

    public static ItemCannabisShredded instance() {
        if(instance == null) instance = new ItemCannabisShredded();
        return instance;
    }

}
