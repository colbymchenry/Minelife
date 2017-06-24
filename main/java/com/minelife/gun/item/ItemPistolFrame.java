package com.minelife.gun.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class ItemPistolFrame extends ItemGunPart {

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "GTB",
                'G', ItemGunPart.grip,
                'T', ItemGunPart.trigger,
                'B', ItemGunPart.pistolBarrel);
    }
}
