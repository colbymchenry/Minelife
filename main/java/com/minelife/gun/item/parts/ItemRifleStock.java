package com.minelife.gun.item.parts;

import com.minelife.gun.item.ItemGunmetal;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class ItemRifleStock extends ItemGunPart {

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "G  ",
                "GGG",
                "G  ",
                'G', ItemGunmetal.getItem());
    }

}