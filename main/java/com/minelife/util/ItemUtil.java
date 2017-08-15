package com.minelife.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemUtil {

    public static String itemToString(ItemStack itemStack)
    {
        NBTTagCompound tagCompound = itemStack.writeToNBT(new NBTTagCompound());
        return tagCompound.toString();
    }

    public static ItemStack itemFromString(String s)
    {
        return ItemStack.loadItemStackFromNBT(NBTUtil.fromString(s));
    }

}