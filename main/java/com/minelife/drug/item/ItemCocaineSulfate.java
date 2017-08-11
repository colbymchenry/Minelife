package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.item.Item;

public class ItemCocaineSulfate extends Item {

    public ItemCocaineSulfate() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("cocaine_sulfate");
        setTextureName(Minelife.MOD_ID + ":cocaine_sulfate");
    }

}
