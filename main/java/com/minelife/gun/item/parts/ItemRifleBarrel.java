package com.minelife.gun.item.parts;

import com.minelife.Minelife;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

// two pistol barrels
public class ItemRifleBarrel extends ItemGunPart {

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "GGG",
                'G', Minelife.items.pistol_barrel);
    }

}
