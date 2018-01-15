package com.minelife.util;

import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Field;
import java.util.List;

public class ItemHelper {

    public static String itemToString(ItemStack itemStack)
    {
        NBTTagCompound tagCompound = itemStack.writeToNBT(new NBTTagCompound());
        return tagCompound.toString();
    }

    public static ItemStack itemFromString(String s)
    {
        return ItemStack.loadItemStackFromNBT(NBTUtil.fromString(s));
    }

    public static List<ItemStack> getStacks(Item item, int amount) {
        List<ItemStack> stacks = Lists.newArrayList();
        int maxStackSize = 0;

        // get item max stack size
        try {
            Field field = Item.class.getDeclaredField("maxStackSize");
            field.setAccessible(true);
            Object value = field.get(item);
            maxStackSize = (int) value;
        }catch (Exception e) {
            e.printStackTrace();
        }

        // return an empty array since we couldn't get the max stack size
        if(maxStackSize == 0) {
            return stacks;
        }

        int completeStacks = (int) Math.floor(amount / maxStackSize);
        int leftOver = amount - (completeStacks * maxStackSize);

        for (int i = 0; i < completeStacks; i++) stacks.add(new ItemStack(item, maxStackSize));
        stacks.add(new ItemStack(item, leftOver));
        return stacks;
    }

}