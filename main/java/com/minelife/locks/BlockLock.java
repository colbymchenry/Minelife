package com.minelife.locks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLock extends BlockContainer {

    private LockType lockType;

    public BlockLock(LockType lockType) {
        super(Material.iron);
        this.lockType = lockType;
        setBlockName("lock_" + lockType.name().toLowerCase());
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        TileEntityLock tileLock = new TileEntityLock();
        tileLock.lockType = lockType;
        return tileLock;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return -1;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if(world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityLock) {
            TileEntityLock tileLock = (TileEntityLock) world.getTileEntity(x, y, z);
            if(tileLock.getWorldObj().getBlock(tileLock.protectX, tileLock.protectY, tileLock.protectZ) != tileLock.protectedBlockType) {
                breakBlock(world, x, y, z, block, 0);
            }
        }
    }

}
