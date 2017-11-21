package com.minelife.shop;

import buildcraft.core.lib.block.BlockBuildCraft;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTrader extends BlockBuildCraft {

    public BlockTrader() {
        super(Material.iron, CreativeTabs.tabRedstone);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return null;
    }
}
