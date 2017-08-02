package com.minelife.drug.item;

import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemAmmonia extends Item {

    private static ItemAmmonia instance;

    private ItemAmmonia() {
        setCreativeTab(CreativeTabs.tabBrewing);
        setTextureName(Minelife.MOD_ID + ":ammonia");
    }

    public static ItemAmmonia instance() {
        if(instance == null) instance = new ItemAmmonia();
        return instance;
    }

}
