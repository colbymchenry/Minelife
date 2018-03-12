package com.minelife.locks;

import com.minelife.Minelife;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockSign;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLock extends BlockContainer {

    private LockType lockType;
    private IIcon icon;

    public BlockLock(LockType lockType) {
        super(Material.iron);
        this.lockType = lockType;
        setBlockName("lock_" + lockType.name().toLowerCase());
        setBlockBounds(0.3f, 0.80f, 0.05f, 0.7f, 0.85f, 0.1f);
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        icon = register.registerIcon(Minelife.MOD_ID + ":" + lockType.name().toLowerCase() + "_lock");
    }

    public IIcon getIcon() {
        return icon;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        TileEntityLock tileLock = (TileEntityLock) world.getTileEntity(x, y, z);

        if (tileLock.protectZ == tileLock.zCoord - 1) {
            setBlockBounds(0.3f, 0.85f - 0.5f, 0.05f,
                    0.7f, 0.85f, 0.1f);
        } else if (tileLock.protectX == tileLock.xCoord - 1) {
           setBlockBounds(0.1f, 0.85f - 0.5f, 0.3f,
                   0.05f, 0.85f, 0.7f);
        } else if (tileLock.protectZ == tileLock.zCoord + 1) {
            setBlockBounds(0.3f,  0.85f - 0.5f,  1 + 0.05f,
                    0.7f, 0.85f,  1 - 0.1f);
        } else if (tileLock.protectX == tileLock.xCoord + 1) {
            setBlockBounds(1 - 0.1f, 0.85f - 0.5f, 0.3f,
                    1.05f, 0.85f, 0.7f);
        }
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntityLock tileLock = (TileEntityLock) world.getTileEntity(x, y, z);

        if (tileLock.protectZ == tileLock.zCoord - 1) {
            return AxisAlignedBB.getBoundingBox(x + 0.3, y + 0.85 - 0.5, z - 0.05,
                    x + 0.7, y + 0.85, z + 0.1);
        } else if (tileLock.protectX == tileLock.xCoord - 1) {
            return AxisAlignedBB.getBoundingBox(x + 0.1, y + 0.85 - 0.5, z + 0.3,
                    x - 0.05, y + 0.85, z + 0.7);
        } else if (tileLock.protectZ == tileLock.zCoord + 1) {
            return AxisAlignedBB.getBoundingBox(x + 0.3, y + 0.85 - 0.5, z + 1 + 0.05,
                    x + 0.7, y + 0.85, z + 1 - 0.1);
        } else if (tileLock.protectX == tileLock.xCoord + 1) {
            return AxisAlignedBB.getBoundingBox(x + 1 - 0.1, y + 0.85 - 0.5, z + 0.3,
                    x + 1.05, y + 0.85, z + 0.7);
        }
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
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
        if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityLock) {
            TileEntityLock tileLock = (TileEntityLock) world.getTileEntity(x, y, z);
            if (tileLock.getWorldObj().getBlock(tileLock.protectX, tileLock.protectY, tileLock.protectZ) != tileLock.protectedBlockType) {
                breakBlock(world, x, y, z, block, 0);
            }
        }
    }

}
