package com.minelife.gun;

import com.minelife.Minelife;
import net.minecraft.item.Item;

public abstract class ItemAmmo extends Item {

    public ItemAmmo() {
        setUnlocalizedName(getClass().getSimpleName());
        setCreativeTab(ModGun.tabAmmo);
        setTextureName(Minelife.MOD_ID + ":ammo/" + getClass().getSimpleName());
    }

    public abstract int getDamage();

}
