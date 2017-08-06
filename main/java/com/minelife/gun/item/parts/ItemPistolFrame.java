package com.minelife.gun.item.parts;

import com.minelife.MLItems;
import com.minelife.Minelife;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class ItemPistolFrame extends ItemGunPart {

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "GTB",
                'G', MLItems.grip,
                'T', MLItems.trigger,
                'B', MLItems.pistol_barrel);
    }
}
