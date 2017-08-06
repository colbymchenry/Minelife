package com.minelife.gun.item.ammos;

import com.minelife.Minelife;
import com.minelife.gun.ModGun;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public abstract class ItemAmmo extends Item {

    public ItemAmmo() {
        setUnlocalizedName(getClass().getSimpleName());
        setCreativeTab(ModGun.tabAmmo);
        setTextureName(Minelife.MOD_ID + ":ammo/" + getClass().getSimpleName());
    }

    public abstract void registerRecipe();

    public abstract AmmoType getAmmoType();

    public static void registerRecipes() {
        Minelife.items.ammo_556.registerRecipe();
        Minelife.items.ammo_556_explosive.registerRecipe();
        Minelife.items.ammo_556_incendiary.registerRecipe();
        Minelife.items.ammo_pistol.registerRecipe();
        Minelife.items.ammo_pistol_incendiary.registerRecipe();
    }

    public enum AmmoType {
        NORMAL, EXPLOSIVE, INCENDIARY
    }

}
