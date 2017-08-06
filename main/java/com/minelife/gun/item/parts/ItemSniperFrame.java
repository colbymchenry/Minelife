package com.minelife.gun.item.parts;

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
                'S', Minelife.items.sniper_scope,
                'K', Minelife.items.rifle_stock,
                'T', Minelife.items.trigger,
                'B', Minelife.items.sniper_barrel,
                'G', Minelife.items.grip);
    }
}
