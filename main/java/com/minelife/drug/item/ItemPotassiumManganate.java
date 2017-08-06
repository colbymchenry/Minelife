package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.item.Item;

public class ItemPotassiumManganate extends Item {

    public ItemPotassiumManganate() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("potassium_manganate");
        setTextureName(Minelife.MOD_ID + ":potassium_manganate");
    }
    
}
