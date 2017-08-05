package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.item.Item;

public class ItemPyrolusite extends Item {

    private static ItemPyrolusite instance;

    public static ItemPyrolusite instance()
    {
        if(instance == null) instance = new ItemPyrolusite();
        return instance;
    }

    private ItemPyrolusite()
    {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("pyrolusite");
        setTextureName(Minelife.MOD_ID + ":pyrolusite");
    }
}
