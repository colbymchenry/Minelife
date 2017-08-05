package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.item.Item;

public class ItemCalciumHydroxide extends Item {

    private static ItemCalciumHydroxide instance;

    public static ItemCalciumHydroxide instance() {
        if(instance == null) instance = new ItemCalciumHydroxide();
        return instance;
    }

    private ItemCalciumHydroxide() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("calcium_hydroxide");
        setTextureName(Minelife.MOD_ID + ":calcium_hydroxide");
    }

}
