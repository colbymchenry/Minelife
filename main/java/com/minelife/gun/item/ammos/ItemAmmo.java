package com.minelife.gun.item.ammos;

import com.minelife.Minelife;
import com.minelife.gun.ModGun;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public abstract class ItemAmmo extends Item {

    public static Ammo556 ammo556;
    public static Ammo556Explosive ammo556Explosive;
    public static Ammo556Incendiary ammo556Incendiary;
    public static AmmoPistol ammoPistol;
    public static AmmoPistolIncendiary ammoPistolIncendiary;

    public ItemAmmo() {
        setUnlocalizedName(getClass().getSimpleName());
        setCreativeTab(ModGun.tabAmmo);
        setTextureName(Minelife.MOD_ID + ":ammo/" + getClass().getSimpleName());
        GameRegistry.registerItem(this, getClass().getSimpleName());
    }

    public abstract void registerRecipe();

    public abstract AmmoType getAmmoType();

    public static final void registerAmmos() {
        ammo556 = new Ammo556();
        ammo556Explosive = new Ammo556Explosive();
        ammo556Incendiary = new Ammo556Incendiary();
        ammoPistol = new AmmoPistol();
        ammoPistolIncendiary = new AmmoPistolIncendiary();
        registerRecipes();
    }

    public static final void registerRecipes() {
        ammo556.registerRecipe();
        ammo556Explosive.registerRecipe();
        ammo556Incendiary.registerRecipe();
        ammoPistol.registerRecipe();
        ammoPistolIncendiary.registerRecipe();
    }

    public enum AmmoType {
        NORMAL, EXPLOSIVE, INCENDIARY;
    }

}
