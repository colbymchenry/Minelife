package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.item.Item;

public class ItemPotassiumHydroxide extends Item {

    private static ItemPotassiumHydroxide instance;

    public static ItemPotassiumHydroxide instance() {
        if(instance == null) instance = new ItemPotassiumHydroxide();
        return instance;
    }

    private ItemPotassiumHydroxide() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("potassium_hydroxide");
        setTextureName(Minelife.MOD_ID + ":potassium_hydroxide");
    }

}
