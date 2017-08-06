package com.minelife.gun.item.parts;

import com.minelife.Minelife;
import com.minelife.gun.ModGun;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public abstract class ItemGunPart extends Item {

    public ItemGunPart() {
        setUnlocalizedName(getClass().getSimpleName());
        setTextureName(Minelife.MOD_ID + ":gunparts/" + getClass().getSimpleName());
        setCreativeTab(ModGun.tabGuns);
    }

    public abstract void registerRecipe();

    public static void registerRecipes() {
        Minelife.items.grip.registerRecipe();
        Minelife.items.pistol_barrel.registerRecipe();
        Minelife.items.pistol_frame.registerRecipe();
        Minelife.items.rifle_barrel.registerRecipe();
        Minelife.items.rifle_stock.registerRecipe();
        Minelife.items.rifle_frame.registerRecipe();
        Minelife.items.sniper_barrel.registerRecipe();
        Minelife.items.sniper_scope.registerRecipe();
        Minelife.items.sniper_frame.registerRecipe();
        Minelife.items.trigger.registerRecipe();
    }

}
