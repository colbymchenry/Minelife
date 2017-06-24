package com.minelife.gun.item;

import com.minelife.Minelife;
import com.minelife.gun.ModGun;
import net.minecraft.item.Item;

public class ItemZincIngot extends Item {

    private static ItemZincIngot instance;

    private ItemZincIngot() {
        setUnlocalizedName("zincIngot");
        setTextureName(Minelife.MOD_ID + ":zinc_ingot");
        setCreativeTab(ModGun.tabGuns);
    }

    public static final ItemZincIngot getItem() {
        if(instance == null) instance = new ItemZincIngot();
        return instance;
    }

}
