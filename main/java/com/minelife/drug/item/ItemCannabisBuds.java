package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemCannabisBuds extends Item {

    private static ItemCannabisBuds instance;

    private ItemCannabisBuds(){
        setCreativeTab(ModDrugs.tab_drugs);
        setTextureName(Minelife.MOD_ID + ":buds");
        setUnlocalizedName("cannabis_buds");
    }

    public static ItemCannabisBuds instance() {
        if(instance == null) instance = new ItemCannabisBuds();
        return instance;
    }

}
