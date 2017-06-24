package com.minelife.gun.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSniperFrame extends ItemGunPart {

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                " S ",
                "KTB",
                " G ",
                'S', ItemGunPart.sniperScope,
                'K', ItemGunPart.rifleStock,
                'T', ItemGunPart.trigger,
                'B', ItemGunPart.sniperBarrel,
                'G', ItemGunPart.grip);
    }
}
