package com.minelife.gun.item;

import com.minelife.Minelife;
import com.minelife.gun.ModGun;
import net.minecraft.item.Item;

public class ItemZincPlate extends Item {

    public static ItemZincPlate instance;

    public ItemZincPlate() {
        setUnlocalizedName("zincPlate");
        setTextureName(Minelife.MOD_ID + ":zinc_plate");
        instance = this;
        setCreativeTab(ModGun.tabGuns);
    }
}
