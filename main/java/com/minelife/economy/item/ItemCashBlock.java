package com.minelife.economy.item;

import com.minelife.Minelife;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemCashBlock extends ItemBlock {

    public ItemCashBlock(Block block) {
        super(block);
        setRegistryName("cashBlock");
        setUnlocalizedName(Minelife.MOD_ID + ":cashBlock");
    }

}
