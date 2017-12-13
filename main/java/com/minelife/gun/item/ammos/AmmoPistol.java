package com.minelife.gun.item.ammos;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.Ic2Items;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class AmmoPistol extends ItemAmmo {
    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this, 16),
                " L ",
                "CGC",
                'C', Ic2Items.copperIngot,
                'G', Items.gunpowder,
                'L', Ic2Items.leadIngot);
    }

    @Override
    public AmmoType getAmmoType() {
        return AmmoType.NORMAL;
    }
}
