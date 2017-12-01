package com.minelife.police;

import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemHandcuffs extends Item {

    public ItemHandcuffs() {
        setCreativeTab(CreativeTabs.tabMisc);
        setTextureName(Minelife.MOD_ID + ":handcuffs");
        setUnlocalizedName("handcuffs");
    }
}
