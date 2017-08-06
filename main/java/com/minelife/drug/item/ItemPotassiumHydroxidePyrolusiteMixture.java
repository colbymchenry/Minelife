package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.item.Item;

public class ItemPotassiumHydroxidePyrolusiteMixture extends Item {

    public ItemPotassiumHydroxidePyrolusiteMixture() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("potassium_hydroxide_pyrolusite_mixture");
        setTextureName(Minelife.MOD_ID + ":potassium_hydroxide_pyrolusite_mixture");
    }

}
