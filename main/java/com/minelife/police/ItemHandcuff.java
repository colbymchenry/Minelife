package com.minelife.police;

import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemHandcuff extends Item {

    public static final ItemHandcuff INSTANCE = new ItemHandcuff();
    public static final String NAME = "handcuffs";

    private ItemHandcuff() {
        setTextureName(Minelife.MOD_ID + ":" + NAME);
        setCreativeTab(CreativeTabs.tabMisc);
        setUnlocalizedName(NAME);
    }

}
