package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.item.Item;

public class ItemAbstractDrug extends Item {

    public ItemAbstractDrug(String unlocalized_name) {
        setCreativeTab(ModDrugs.tab_drugs);
        setTextureName(Minelife.MOD_ID + ":" + unlocalized_name);
        setUnlocalizedName(unlocalized_name);
    }

}
