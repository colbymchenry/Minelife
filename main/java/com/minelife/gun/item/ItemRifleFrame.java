package com.minelife.gun.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemRifleFrame extends Item {

    public static ItemRifleFrame instance;

    public ItemRifleFrame() {
        instance = this;
    }

    public static void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(ItemRifleFrame.instance),
                "STB",
                " G ",
                'S', ItemRifleStock.instance,
                'T', ItemTrigger.instance,
                'B', ItemRifleBarrel.instance,
                'G', ItemGrip.instance);
    }

}
