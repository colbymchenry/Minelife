package com.minelife.gun.item.ammos;

import com.minelife.Minelife;
import com.minelife.gun.ModGun;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public abstract class ItemAmmo extends Item {

    public static AmmoAK47 ak47;
    public static AmmoAWP awp;
    public static AmmoBarrett barrett;
    public static AmmoDesertEagle desertEagle;
    public static AmmoM4A4 m4A4;
    public static AmmoMagnum magnum;

    public ItemAmmo() {
        setUnlocalizedName(getClass().getSimpleName());
        setCreativeTab(ModGun.tabAmmo);
        setTextureName(Minelife.MOD_ID + ":ammo/" + getClass().getSimpleName());
        GameRegistry.registerItem(this, getClass().getSimpleName());
    }

    public abstract void registerRecipe();

    public static final void registerAmmos() {
        ak47 = new AmmoAK47();
        awp = new AmmoAWP();
        barrett = new AmmoBarrett();
        desertEagle = new AmmoDesertEagle();
        m4A4 = new AmmoM4A4();
        magnum = new AmmoMagnum();
    }

    public static final void registerRecipes() {
        ak47.registerRecipe();
        awp.registerRecipe();
        barrett.registerRecipe();
        desertEagle.registerRecipe();
        m4A4.registerRecipe();
        magnum.registerRecipe();
        registerRecipes();
    }

}
