package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.item.Item;

public class ItemWaxyCocaine extends Item {

    public ItemWaxyCocaine() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("waxy_cocaine");
        setTextureName(Minelife.MOD_ID + ":waxy_cocaine");
    }

}
