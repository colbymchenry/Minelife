package com.minelife.gun.item.ammos;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.Ic2Items;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Ammo556Incendiary extends ItemAmmo {
    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this, 2),
                "FLF",
                "CGC",
                "CCC",
                'C', Ic2Items.copperIngot,
                'G', Items.gunpowder,
                'L', Ic2Items.leadIngot,
                'F', Items.flint_and_steel);
    }

    @Override
    public AmmoType getAmmoType() {
        return AmmoType.INCENDIARY;
    }
}
