package com.minelife.gun.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

// two pistol barrels
public class ItemRifleBarrel extends Item {

    public static ItemRifleBarrel instance;

    public ItemRifleBarrel() {
        instance = this;
    }

    public static void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(ItemRifleBarrel.instance),
                "GGG",
                'G', ItemPistolBarrel.instance);
    }

}
