package com.minelife.gun;

import com.minelife.Minelife;
import net.minecraft.item.Item;

public abstract class BaseAmmo extends Item {

    public BaseAmmo() {
        setUnlocalizedName(getName());
        setCreativeTab(ModGun.tabAmmo);
        setTextureName(Minelife.MOD_ID + ":ammo/" + getName());
    }

    public abstract String getName();

}
