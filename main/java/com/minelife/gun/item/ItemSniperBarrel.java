package com.minelife.gun.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSniperBarrel extends Item {

    public static ItemSniperBarrel instance;

    public ItemSniperBarrel() {
        instance = this;
    }

    public static void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(ItemSniperBarrel.instance),
                "GGG",
                'G', ItemRifleBarrel.instance);
    }

}
