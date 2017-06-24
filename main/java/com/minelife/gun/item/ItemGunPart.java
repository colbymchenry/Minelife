package com.minelife.gun.item;

import com.minelife.Minelife;
import com.minelife.gun.ModGun;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public abstract class ItemGunPart extends Item {

    public static ItemPistolBarrel pistolBarrel;
    public static ItemPistolFrame pistolFrame;
    public static ItemRifleBarrel rifleBarrel;
    public static ItemRifleFrame rifleFrame;
    public static ItemRifleStock rifleStock;
    public static ItemSniperBarrel sniperBarrel;
    public static ItemSniperFrame sniperFrame;
    public static ItemSniperScope sniperScope;
    public static ItemTrigger trigger;
    public static ItemGrip grip;

    public ItemGunPart() {
        setUnlocalizedName(getClass().getSimpleName());
        setTextureName(Minelife.MOD_ID + ":gunparts/" + getClass().getSimpleName());
        setCreativeTab(ModGun.tabGuns);
        GameRegistry.registerItem(this, getClass().getSimpleName());
    }

    public abstract void registerRecipe();

    public static final void registerRecipes() {
        grip.registerRecipe();
        pistolBarrel.registerRecipe();
        pistolFrame.registerRecipe();
        rifleBarrel.registerRecipe();
        rifleStock.registerRecipe();
        rifleFrame.registerRecipe();
        sniperBarrel.registerRecipe();
        sniperScope.registerRecipe();
        sniperFrame.registerRecipe();
        trigger.registerRecipe();
    }

    public static final void registerParts() {
        grip = new ItemGrip();
        pistolBarrel = new ItemPistolBarrel();
        pistolFrame = new ItemPistolFrame();
        rifleBarrel = new ItemRifleBarrel();
        rifleStock = new ItemRifleStock();
        rifleFrame = new ItemRifleFrame();
        sniperBarrel = new ItemSniperBarrel();
        sniperScope = new ItemSniperScope();
        sniperFrame = new ItemSniperFrame();
        trigger = new ItemTrigger();
    }

}
