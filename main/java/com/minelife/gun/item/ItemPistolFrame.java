package com.minelife.gun.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

// TODO: Use ForgeHammer to put together this item
// TODO: AK47 Iron Blocks on bottom and Gold Gears in middle with logs on top
// TODO: AWP Gold Blocks on bottom and Diamond Gears in middle with Scope on top and Gold Gears in top left and right
// TODO: Barrett Diamond Blocks on bottom and Diamond Gears in middle with Scope on top and Diamond Gears in top left and right
// TODO: M4A4 Iron Blocks on bottom and Diamond Gears in middle with logs on top
// TODO: DesertEagle
// TODO: Magnum
public class ItemPistolFrame extends Item {

    public static ItemPistolFrame instance;

    public ItemPistolFrame() {
        instance = this;
    }

    public static void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(ItemPistolFrame.instance),
                "GTB",
                'G', ItemGrip.instance,
                'T', ItemTrigger.instance,
                'B', ItemPistolBarrel.instance);
    }

}
