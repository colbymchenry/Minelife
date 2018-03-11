package com.minelife.locks;

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
        setTextureName(Minelife.MOD_ID + ":lock_" + lockType.name().toLowerCase());
    }

    @Override
    public boolean onItemUse(ItemStack heldItem, EntityPlayer player, World world, int blockX, int blockY, int blockZ, int side, float exactX, float exactY, float exactZ) {
        System.out.println(side);
        switch(side) {
            case 0:
                // stop placement
                break;
            case 1:
                // stop placement
                break;
            case 2:
                // subtract z
                break;
            case 3:
                // add z
                break;
            case 4:
                // subtract x
                break;
            case 5:
                // add x
                break;
        }
        return true;
    }
}
