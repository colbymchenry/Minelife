package com.minelife.drug.item;

import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemCocaLeaf extends Item {

    private static ItemCocaLeaf instance, instance_dry;
    private boolean dry;

    private ItemCocaLeaf(boolean dry)
    {
        setCreativeTab(CreativeTabs.tabDecorations);
        setTextureName(Minelife.MOD_ID + ":coca_leaf" + (dry ? "_dry" : ""));
    }

    public static ItemCocaLeaf instance(boolean dry) {
        if(dry) {
            if(instance_dry == null) instance_dry = new ItemCocaLeaf(true);
            return instance_dry;
        } else {
            if (instance == null) instance = new ItemCocaLeaf(false);
            return instance;
        }
    }

    public boolean dry() { return dry; }

}
