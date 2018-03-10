package com.minelife.locks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLock extends BlockContainer {

    public BlockLock() {
        super(Material.iron);
        setBlockName("lock");
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityLock();
    }
}
