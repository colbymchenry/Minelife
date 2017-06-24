package com.minelife.gun.item;

import com.minelife.Minelife;
import com.minelife.gun.ModGun;
import net.minecraft.item.Item;

public class ItemZincPlate extends Item {

    private static ItemZincPlate instance;

    private ItemZincPlate() {
        setUnlocalizedName("zincPlate");
        setTextureName(Minelife.MOD_ID + ":zinc_plate");
        setCreativeTab(ModGun.tabGuns);
    }

    public static final ItemZincPlate getItem() {
        if(instance == null) instance = new ItemZincPlate();
        return instance;
    }
}
