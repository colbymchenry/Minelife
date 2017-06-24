package com.minelife.gun.item;

import com.minelife.Minelife;
import com.minelife.gun.ModGun;
import net.minecraft.item.Item;

public class ItemGunmetal extends Item {

    private static ItemGunmetal instance;

    private ItemGunmetal() {
        setUnlocalizedName("gunmetal");
        setTextureName(Minelife.MOD_ID + ":gunmetal");
        setCreativeTab(ModGun.tabGuns);
    }

    public static final ItemGunmetal getItem() {
        if(instance == null) instance = new ItemGunmetal();

        return instance;
    }
}
