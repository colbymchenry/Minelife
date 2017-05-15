package com.minelife.gun.item;

import com.minelife.Minelife;
import com.minelife.gun.ModGun;
import net.minecraft.item.Item;

public class ItemZincIngot extends Item {

    public static ItemZincIngot instance;

    public ItemZincIngot() {
        setUnlocalizedName("zincIngot");
        setTextureName(Minelife.MOD_ID + ":zinc_ingot");
        instance = this;
        setCreativeTab(ModGun.tabGuns);
    }

}
