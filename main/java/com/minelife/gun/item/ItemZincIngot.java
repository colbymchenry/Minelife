package com.minelife.gun.item;

import com.minelife.Minelife;
import com.minelife.gun.ModGun;
import net.minecraft.item.Item;

public class ItemZincIngot extends Item {

    public ItemZincIngot() {
        setUnlocalizedName("zinc_ingot");
        setTextureName(Minelife.MOD_ID + ":zinc_ingot");
        setCreativeTab(ModGun.tabGuns);
    }
}
