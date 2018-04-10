package com.minelife.drugs.item;

import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemGrinder extends Item {

    public ItemGrinder() {
        setRegistryName(Minelife.MOD_ID, "grinder");
        setUnlocalizedName(Minelife.MOD_ID + ":grinder");
        setCreativeTab(CreativeTabs.MISC);
        setMaxDamage(100);
        setContainerItem(this);
        setMaxStackSize(1);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        if(itemStack.getItemDamage() + 1 >= itemStack.getMaxDamage()) return ItemStack.EMPTY;
        itemStack = itemStack.copy();
        itemStack.setItemDamage(itemStack.getItemDamage() + 1);
        return itemStack;
    }
}
