package com.minelife.gun.item;

import com.minelife.Minelife;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemTrigger extends Item {

    public static ItemTrigger instance;

    public ItemTrigger() {
        instance = this;
        setUnlocalizedName("trigger");
        setTextureName(Minelife.MOD_ID + ":gunparts/Trigger");
    }

    public static void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(ItemTrigger.instance),
                " G ",
                " G ",
                "  G",
                'G', ItemGunmetal.instance);
    }

}
