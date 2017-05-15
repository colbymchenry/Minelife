package com.minelife.gun.item;

import com.minelife.Minelife;
import com.minelife.gun.BaseGun;
import net.minecraft.item.Item;

public class ItemMagazine extends Item {

    public ItemMagazine(BaseGun gun) {
        setUnlocalizedName(gun.getName() + "Magazine");
        setTextureName(Minelife.MOD_ID + ":gunparts/" + gun.getName() + "_Magazine");
    }

}
