package com.minelife.gun.item;

import com.minelife.Minelife;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemGrip extends Item {

    public static ItemGrip instance;

    public ItemGrip() {
        instance = this;
        setUnlocalizedName("grip");
        setTextureName(Minelife.MOD_ID + ":gunparts/Grip");
    }

    public static void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(ItemGrip.instance),
                " G ",
                " G ",
                'G', ItemGunmetal.instance);
    }

}
