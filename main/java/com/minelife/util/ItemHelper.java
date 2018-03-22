package com.minelife.util;

import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class ItemHelper {

    public static String itemToString(ItemStack itemStack)
    {
        NBTTagCompound tagCompound = itemStack.writeToNBT(new NBTTagCompound());
        return tagCompound.toString();
    }

    public static ItemStack itemFromString(String s)
    {
        return new ItemStack(NBTHelper.fromString(s));
    }

    public static List<ItemStack> getStacks(Item item, int amount, int meta) {
        List<ItemStack> stacks = Lists.newArrayList();
        int maxStackSize = new ItemStack(item).getMaxStackSize();

        // return an empty array since we couldn't get the max stack size
        if(maxStackSize == 0) {
            return stacks;
        }

        int completeStacks = (int) Math.floor(amount / maxStackSize);
        int leftOver = amount - (completeStacks * maxStackSize);

        for (int i = 0; i < completeStacks; i++) stacks.add(new ItemStack(item, maxStackSize, meta));
        stacks.add(new ItemStack(item, leftOver, meta));
        return stacks;
    }

}