package com.minelife.locks;

import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLockPick extends Item {

    public ItemLockPick() {
        setUnlocalizedName("lockPick");
        setTextureName(Minelife.MOD_ID + ":lock_pick");
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public boolean onItemUse(ItemStack heldStack, EntityPlayer player, World world, int blockX, int blockY, int blockZ, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        return false;
    }
}
