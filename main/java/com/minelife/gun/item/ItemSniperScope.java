package com.minelife.gun.item;

import com.minelife.Minelife;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSniperScope extends Item {

    public static ItemSniperScope instance;

    public ItemSniperScope() {
        instance = this;
        setUnlocalizedName("sniperScope");
        setTextureName(Minelife.MOD_ID + ":gunparts/SniperScope");
    }

    public static void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(ItemSniperScope.instance),
                "GMG",
                'G', Item.getItemFromBlock(Blocks.glass_pane),
                'M', ItemGunmetal.instance);
    }

}
