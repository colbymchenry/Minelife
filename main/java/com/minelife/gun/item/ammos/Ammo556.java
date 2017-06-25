package com.minelife.gun.item.ammos;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.Ic2Items;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class Ammo556 extends ItemAmmo {
    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                " L ",
                "CGC",
                "CCC",
                'C', Ic2Items.copperIngot,
                'G', Items.gunpowder,
                'L', Ic2Items.leadIngot);
    }

    @Override
    public AmmoType getAmmoType() {
        return AmmoType.DEFAULT;
    }
}
