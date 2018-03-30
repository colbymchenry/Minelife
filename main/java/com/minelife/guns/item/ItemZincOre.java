package com.minelife.guns.item;

import com.minelife.Minelife;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;

public class ItemZincOre extends ItemBlock {

    public ItemZincOre(Block block) {
        super(block);
        setRegistryName(Minelife.MOD_ID, "zinc_ore");
        setUnlocalizedName(Minelife.MOD_ID + ":zinc_ore");
        setCreativeTab(CreativeTabs.MISC);
    }

}