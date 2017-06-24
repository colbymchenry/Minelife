package com.minelife.gun.item;

import com.minelife.Minelife;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
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
