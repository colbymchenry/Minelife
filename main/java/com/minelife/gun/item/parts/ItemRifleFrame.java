package com.minelife.gun.item.parts;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class ItemRifleFrame extends ItemGunPart {

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "STB",
                " G ",
                'S', ItemGunPart.rifleStock,
                'T', ItemGunPart.trigger,
                'B', ItemGunPart.rifleBarrel,
                'G', ItemGunPart.grip);
    }

}
