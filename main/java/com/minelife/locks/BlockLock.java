package com.minelife.locks;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;
import com.minelife.MLItems;
import com.minelife.Minelife;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockSign;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
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
        setBlockBounds(0.3f, 0.3f, 0.05f, 0.7f, 0.85f, 0.1f);
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

        boolean isDoor = tileLock.getWorldObj().getBlock(tileLock.protectX, tileLock.protectY, tileLock.protectZ) == Blocks.wooden_door;
        boolean isTopDoor = (tileLock.getWorldObj().getBlockMetadata(tileLock.protectX, tileLock.protectY, tileLock.protectZ) & 0x8) != (byte) 0;
        boolean isDoorOpen = false;

        if (isDoor) {
            if (isTopDoor) {
                isDoorOpen = (tileLock.getWorldObj().getBlockMetadata(tileLock.protectX, tileLock.protectY - 1, tileLock.protectZ) & 0x4) == (byte) 4;
            } else {
                isDoorOpen = (tileLock.getWorldObj().getBlockMetadata(tileLock.protectX, tileLock.protectY, tileLock.protectZ) & 0x4) == (byte) 4;
            }
        }

        if (tileLock.protectZ == tileLock.zCoord - 1) {
            if (isDoorOpen) {
                setBlockBounds(0.15f, 0.3f, -0.7f,
                        0.27f, 0.85f, -0.3f);
            } else {
                setBlockBounds(0.3f, 0.3f, -0.05f,
                        0.7f, 0.85f, 0.1f);
            }
        } else if (tileLock.protectX == tileLock.xCoord - 1) {
            if (isDoorOpen) {
                setBlockBounds(-0.7f, 0.3f, 0.72f,
                        -0.3f, 0.85f, 0.85f);
            } else {
                setBlockBounds(-0.05f, 0.3f, 0.3f,
                        0.1f, 0.85f, 0.7f);
            }
        } else if (tileLock.protectZ == tileLock.zCoord + 1) {
            if (isDoorOpen) {
                setBlockBounds(0.73f, 0.3f, 1.3f,
                        0.85f, 0.85f, 1.7f);
            } else {
                setBlockBounds(0.3f, 0.3f, 0.9f,
                        0.7f, 0.85f, 1f);
            }
        } else if (tileLock.protectX == tileLock.xCoord + 1) {
            if (isDoorOpen) {
                setBlockBounds(1.3f, 0.3f, 0.15f,
                        1.7f, 0.85f, 0.3f);
            } else {
                setBlockBounds(1.05f, 0.3f, 0.3f,
                        1.1f, 0.85f, 0.7f);
            }
        }
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntityLock tileLock = (TileEntityLock) world.getTileEntity(x, y, z);

        boolean isDoor = tileLock.getWorldObj().getBlock(tileLock.protectX, tileLock.protectY, tileLock.protectZ) == Blocks.wooden_door;
        boolean isTopDoor = (tileLock.getWorldObj().getBlockMetadata(tileLock.protectX, tileLock.protectY, tileLock.protectZ) & 0x8) != (byte) 0;
        boolean isDoorOpen = false;

        if (isDoor) {
            if (isTopDoor) {
                isDoorOpen = (tileLock.getWorldObj().getBlockMetadata(tileLock.protectX, tileLock.protectY - 1, tileLock.protectZ) & 0x4) == (byte) 4;
            } else {
                isDoorOpen = (tileLock.getWorldObj().getBlockMetadata(tileLock.protectX, tileLock.protectY, tileLock.protectZ) & 0x4) == (byte) 4;
            }
        }

        if (tileLock.protectZ == tileLock.zCoord - 1) {
            if (isDoorOpen) {
                return AxisAlignedBB.getBoundingBox(x + 0.15, y + 0.3, z - 0.7,
                        x + 0.27, y + 0.85, z - 0.3);
            } else {
                return AxisAlignedBB.getBoundingBox(x + 0.3, y + 0.3, z - 0.05,
                        x + 0.7, y + 0.85, z + 0.1);
            }
        } else if (tileLock.protectX == tileLock.xCoord - 1) {
            if (isDoorOpen) {
                return AxisAlignedBB.getBoundingBox(x - 0.7, y + 0.3, z + 0.72,
                        x - 0.3, y + 0.85, z + 0.85);
            } else {
                return AxisAlignedBB.getBoundingBox(x - 0.05, y + 0.3, z + 0.3,
                        x + 0.1, y + 0.85, z + 0.7);
            }
        } else if (tileLock.protectZ == tileLock.zCoord + 1) {
            if (isDoorOpen) {
                return AxisAlignedBB.getBoundingBox(x + 0.73, y + 0.3, z + 1.3,
                        x + 0.85, y + 0.85, z + 1.7);
            } else {
                return AxisAlignedBB.getBoundingBox(x + 0.3, y + 0.3, z + 0.9,
                        x + 0.7, y + 0.85, z + 1);
            }
        } else if (tileLock.protectX == tileLock.xCoord + 1) {
            if (isDoorOpen) {
                return AxisAlignedBB.getBoundingBox(x + 1.3, y + 0.3, z + 0.15,
                        x + 1.7, y + 0.85, z + 0.3);
            } else {
                return AxisAlignedBB.getBoundingBox(x + 1.05, y + 0.3, z + 0.3,
                        x + 1.1, y + 0.85, z + 0.7);
            }
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
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world, x, y, z, block, meta);
        ItemStack stackToDrop = null;
        if (lockType == LockType.IRON) stackToDrop = new ItemStack(MLItems.ironLock);
        if (lockType == LockType.GOLD) stackToDrop = new ItemStack(MLItems.goldLock);
        if (lockType == LockType.OBSIDIAN) stackToDrop = new ItemStack(MLItems.obsidianLock);
        if (lockType == LockType.DIAMOND) stackToDrop = new ItemStack(MLItems.diamondLock);

        InventoryUtils.dropItem(stackToDrop, world, new Vector3(x, y + 0.25, z));
        world.setBlockToAir(x, y, z);
    }

    // TODO: for some reason keeps dropping two locks
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
