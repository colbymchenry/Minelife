package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.item.Item;

public class ItemProcessedCocaine extends Item {

    public ItemProcessedCocaine() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("processed_cocaine");
        setTextureName(Minelife.MOD_ID + ":processed_cocaine");
    }

}
