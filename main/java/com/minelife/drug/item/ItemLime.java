package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemLime extends Item {

    private static ItemLime instance;

    private ItemLime() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("lime");
        setTextureName(Minelife.MOD_ID + ":lime");
    }

    public static ItemLime instance() {
        if(instance == null) instance = new ItemLime();
        return instance;
    }

}
