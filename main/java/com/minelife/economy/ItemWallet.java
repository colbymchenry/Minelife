package com.minelife.economy;

import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemWallet extends Item {

    public ItemWallet() {
        setCreativeTab(CreativeTabs.tabMisc);
        setUnlocalizedName("wallet");
        setTextureName(Minelife.MOD_ID + ":wallet");
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote)
        {
            player.openGui(Minelife.instance, 80098, world, 0, 0, 0);
        }
        return stack;
    }
}
