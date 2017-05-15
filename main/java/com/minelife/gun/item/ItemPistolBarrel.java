package com.minelife.gun.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemPistolBarrel extends Item {

    public static ItemPistolBarrel instance;

    public ItemPistolBarrel() {
        instance = this;
    }

    public static void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(ItemPistolBarrel.instance),
                "GGG",
                'G', ItemGunmetal.instance);
    }

}
