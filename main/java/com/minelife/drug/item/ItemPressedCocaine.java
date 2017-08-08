package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.item.Item;

public class ItemPressedCocaine extends Item {

    public ItemPressedCocaine() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("pressed_cocaine");
        setTextureName(Minelife.MOD_ID + ":pressed_cocaine");
    }

}
