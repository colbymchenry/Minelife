package com.minelife.gun.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSniperFrame extends Item {

    public static ItemSniperFrame instance;

    public ItemSniperFrame() {
        instance = this;
    }

    public static void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(ItemSniperFrame.instance),
                " S ",
                "KTB",
                " G ",
                'S', ItemSniperScope.instance,
                'K', ItemRifleStock.instance,
                'T', ItemTrigger.instance,
                'B', ItemSniperBarrel.instance,
                'G', ItemGrip.instance);
    }


}
