package com.minelife.drug.item;

import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemCannabisBuds extends Item {

    private static ItemCannabisBuds instance;

    private ItemCannabisBuds(){
        setCreativeTab(CreativeTabs.tabFood);
        setTextureName(Minelife.MOD_ID + ":buds");
    }

    public static ItemCannabisBuds instance() {
        if(instance == null) instance = new ItemCannabisBuds();
        return instance;
    }

}
