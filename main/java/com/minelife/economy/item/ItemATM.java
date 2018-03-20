package com.minelife.economy.item;

import com.minelife.Minelife;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemATM extends ItemBlock {

    public ItemATM(Block block) {
        super(block);
        setRegistryName("atm");
        setUnlocalizedName(Minelife.MOD_ID + ":atm");
    }

}
