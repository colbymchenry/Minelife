package com.minelife.util.blocks;

import com.minelife.Minelife;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlowstone;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockRedstoneLampOn extends Block {

    public BlockRedstoneLampOn() {
        super(Material.glass);
        this.setLightLevel(1.0F);
        setBlockTextureName(Minelife.MOD_ID + ":redstone_lamp_on");
        setBlockName("redstone_lamp_on");
        setCreativeTab(CreativeTabs.tabDecorations);
    }
}
