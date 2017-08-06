package com.minelife.gun.item.parts;

import com.minelife.MLItems;
import com.minelife.Minelife;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class ItemRifleFrame extends ItemGunPart {

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "STB",
                " G ",
                'S', MLItems.rifle_stock,
                'T', MLItems.trigger,
                'B', MLItems.rifle_barrel,
                'G', MLItems.grip);
    }

}
