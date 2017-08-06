package com.minelife.gun.item.parts;

import com.minelife.Minelife;
import com.minelife.gun.item.ItemGunmetal;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class ItemPistolBarrel extends ItemGunPart {

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "GGG",
                'G', Minelife.items.gunmetal);
    }
}
