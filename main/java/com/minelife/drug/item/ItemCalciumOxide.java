package com.minelife.drug.item;

import com.minelife.drug.ModDrugs;
import net.minecraft.item.Item;

public class ItemCalciumOxide extends Item {

    private static ItemCalciumOxide instance;

    public static ItemCalciumOxide instance() {
        if(instance == null) instance = new ItemCalciumOxide();
        return instance;
    }

    private ItemCalciumOxide() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("calcium_oxide");
        setTextureName("calcium_oxide");
    }

}
