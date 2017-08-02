package com.minelife.drug.item;

import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemGrinder extends Item {

    private static ItemGrinder instance;

    private ItemGrinder() {
        setCreativeTab(CreativeTabs.tabBrewing);
        setTextureName(Minelife.MOD_ID + ":grinder");
    }

    public static ItemGrinder instance() {
        if(instance == null) instance = new ItemGrinder();
        return instance;
    }

}
