package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.item.Item;

public class ItemPurpleCocaine extends Item {

    public ItemPurpleCocaine() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("purple_cocaine");
        setTextureName(Minelife.MOD_ID + ":purple_cocaine");
    }

}
