package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemLime extends Item {

    public ItemLime() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("lime");
        setTextureName(Minelife.MOD_ID + ":lime");
    }

}
