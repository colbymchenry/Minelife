package com.minelife.gun.item.parts;

import com.minelife.Minelife;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class ItemRifleFrame extends ItemGunPart {

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "STB",
                " G ",
                'S', Minelife.items.rifle_stock,
                'T', Minelife.items.trigger,
                'B', Minelife.items.rifle_barrel,
                'G', Minelife.items.grip);
    }

}
