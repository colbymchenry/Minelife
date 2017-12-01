package com.minelife.shop;

import buildcraft.core.lib.block.BlockBuildCraft;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockShopBlock extends BlockBuildCraft {

    public BlockShopBlock() {
        super(Material.iron, CreativeTabs.tabRedstone);
        setBlockName("shopBlock");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityShopBlock();
    }
}
