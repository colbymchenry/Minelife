package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemSulfur extends Item {

    private static ItemSulfur instance;

    private ItemSulfur() {
        setCreativeTab(ModDrugs.tab_drugs);
        setTextureName(Minelife.MOD_ID + ":sulfur");
        setUnlocalizedName("sulfur");
    }

    public static ItemSulfur instance() {
        if(instance == null) instance = new ItemSulfur();
        return instance;
    }

}
