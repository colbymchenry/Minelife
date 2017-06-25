package com.minelife.gun.item.ammos;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.Ic2Items;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Ammo556Explosive extends ItemAmmo {
    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "TLT",
                "CGC",
                "CCC",
                'C', Ic2Items.copperIngot,
                'G', Items.gunpowder,
                'L', Ic2Items.leadIngot,
                'T', Item.getItemFromBlock(Blocks.tnt));
    }

    @Override
    public AmmoType getAmmoType() {
        return AmmoType.EXPLOSIVE;
    }
}
