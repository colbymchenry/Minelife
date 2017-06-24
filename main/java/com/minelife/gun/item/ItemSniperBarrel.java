package com.minelife.gun.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSniperBarrel extends ItemGunPart {

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "GGG",
                'G', ItemGunPart.rifleBarrel);
    }

}
