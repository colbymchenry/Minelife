package com.minelife.gun.item.parts;

import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.gun.item.ItemGunmetal;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSniperScope extends ItemGunPart {

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "GMG",
                'G', Item.getItemFromBlock(Blocks.glass_pane),
                'M', MLItems.gunmetal);
    }

}
