package com.minelife.gun.item.parts;

import com.minelife.MLItems;
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
        MLItems.grip.registerRecipe();
        MLItems.pistol_barrel.registerRecipe();
        MLItems.pistol_frame.registerRecipe();
        MLItems.rifle_barrel.registerRecipe();
        MLItems.rifle_stock.registerRecipe();
        MLItems.rifle_frame.registerRecipe();
        MLItems.sniper_barrel.registerRecipe();
        MLItems.sniper_scope.registerRecipe();
        MLItems.sniper_frame.registerRecipe();
        MLItems.trigger.registerRecipe();
    }

}
