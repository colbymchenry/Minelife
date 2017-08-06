package com.minelife.gun.item.parts;

import com.minelife.MLItems;
import com.minelife.Minelife;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class ItemSniperFrame extends ItemGunPart {

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                " S ",
                "KTB",
                " G ",
                'S', MLItems.sniper_scope,
                'K', MLItems.rifle_stock,
                'T', MLItems.trigger,
                'B', MLItems.sniper_barrel,
                'G', MLItems.grip);
    }
}
