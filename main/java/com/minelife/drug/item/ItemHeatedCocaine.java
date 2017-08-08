package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.item.Item;

public class ItemHeatedCocaine extends Item {

    public ItemHeatedCocaine() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("heated_cocaine");
        setTextureName(Minelife.MOD_ID + ":heated_cocaine");
    }

}
