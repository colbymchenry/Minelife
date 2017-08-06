package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.item.Item;

public class ItemCocaPaste extends Item {

    public ItemCocaPaste() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("coca_paste");
        setTextureName(Minelife.MOD_ID + ":coca_paste");
    }

}
