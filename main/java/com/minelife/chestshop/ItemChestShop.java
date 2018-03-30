package com.minelife.chestshop;

import com.minelife.Minelife;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemChestShop extends ItemBlock {

    public ItemChestShop(Block block) {
        super(block);
        setRegistryName(Minelife.MOD_ID, "chest_shop");
        setUnlocalizedName(Minelife.MOD_ID + ":chest_shop");
    }

}
