package com.minelife.locks;

import com.minelife.MLBlocks;
import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLock extends Item {

    private LockType lockType;

    public ItemLock(LockType lockType) {
        this.lockType = lockType;
        setUnlocalizedName("lock_" + lockType.name().toLowerCase());
        setCreativeTab(CreativeTabs.tabMisc);
        setTextureName(Minelife.MOD_ID + ":" + lockType.name().toLowerCase() + "_lock");
    }

    @Override
    public boolean onItemUse(ItemStack heldItem, EntityPlayer player, World world, int blockX, int blockY, int blockZ, int side, float exactX, float exactY, float exactZ) {

        switch (side) {
            case 0:
                // stop placement
                return false;
            case 1:
                // stop placement
                return false;
            case 2:
                // subtract z
                placeLock(world, blockX, blockY, blockZ - 1, blockX, blockY, blockZ, lockType);
                break;
            case 3:
                // add z
                placeLock(world, blockX, blockY, blockZ + 1, blockX, blockY, blockZ, lockType);
                break;
            case 4:
                // subtract x
                placeLock(world, blockX - 1, blockY, blockZ, blockX, blockY, blockZ, lockType);
                break;
            case 5:
                // add x
                placeLock(world, blockX + 1, blockY, blockZ, blockX, blockY, blockZ, lockType);
                break;
        }
        return true;
    }

    private void placeLock(World world, int x, int y, int z, int protectX, int protectY, int protectZ, LockType lockType) {
        world.setBlock(x, y, z, lockType == LockType.IRON ? MLBlocks.ironLock : lockType == LockType.GOLD ? MLBlocks.goldLock :
                lockType == LockType.DIAMOND ? MLBlocks.diamondLock : MLBlocks.obsidianLock);

        TileEntityLock tileEntityLock = (TileEntityLock) world.getTileEntity(x, y, z);
        tileEntityLock.lockType = lockType;
        tileEntityLock.protectX = protectX;
        tileEntityLock.protectY = protectY;
        tileEntityLock.protectZ = protectZ;
        tileEntityLock.protectedBlockType = world.getBlock(protectX, protectY, protectZ);
        tileEntityLock.Sync();
    }
}
