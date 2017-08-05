package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSalt extends Item {

    private static ItemSalt instance;

    private ItemSalt() {
        setCreativeTab(ModDrugs.tab_drugs);
        setTextureName(Minelife.MOD_ID + ":salt");
        setUnlocalizedName("salt");
    }

    public static ItemSalt instance() {
        if(instance == null) instance = new ItemSalt();
        return instance;
    }

    public static void register_recipe() {
        GameRegistry.addSmelting(Items.water_bucket, new ItemStack(instance()), 1F);
    }

}
