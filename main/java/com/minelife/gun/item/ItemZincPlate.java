package com.minelife.gun.item;

import com.minelife.Minelife;
import com.minelife.gun.ModGun;
import net.minecraft.item.Item;

public class ItemZincPlate extends Item {

    public ItemZincPlate() {
        setUnlocalizedName("zinc_plate");
        setTextureName(Minelife.MOD_ID + ":zinc_plate");
        setCreativeTab(ModGun.tabGuns);
    }
}
