package com.minelife.gun.item;

import com.minelife.Minelife;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemRifleStock extends Item {

    public static ItemRifleStock instance;

    public ItemRifleStock() {
        instance = this;
        setUnlocalizedName("rifleStock");
        setTextureName(Minelife.MOD_ID + ":gunparts/RifleStock");
    }

    public static void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(ItemRifleStock.instance),
                "G  ",
                "GGG",
                "G  ",
                'G', ItemGunmetal.instance);
    }

}
