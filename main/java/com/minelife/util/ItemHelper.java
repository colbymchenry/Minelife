package com.minelife.util;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.minebay.ModMinebay;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.Map;

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

    public static Map<Integer, ItemStack> getTotalStacks(IInventory inventory, ItemStack stack) {
        Map<Integer, ItemStack> itemStackMap = Maps.newHashMap();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack1 = inventory.getStackInSlot(i);
            if (stack1 != null && areStacksIdentical(stack1, stack)) itemStackMap.put(i, stack1);
        }
        return itemStackMap;
    }

    public static boolean areStacksIdentical(ItemStack stack1, ItemStack stack2) {
        if (stack1 != null && stack2 != null) {
            return stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage() && Objects.equal(stack1.getTagCompound(), stack2.getTagCompound());
        } else {
            return stack1 == stack2;
        }
    }

}