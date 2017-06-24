package com.minelife.gun.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class ItemTrigger extends ItemGunPart {

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                " G ",
                " G ",
                "  G",
                'G', ItemGunmetal.getItem());
    }

}
