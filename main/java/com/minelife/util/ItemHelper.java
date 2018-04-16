package com.minelife.util;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.minebay.ModMinebay;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemHelper {

    public static String itemToString(ItemStack itemStack) {
        NBTTagCompound tagCompound = itemStack.writeToNBT(new NBTTagCompound());
        return tagCompound.toString();
    }

    public static ItemStack itemFromString(String s) {
        return new ItemStack(NBTHelper.fromString(s));
    }

    public static List<ItemStack> getStacks(Item item, int amount, int meta) {
        List<ItemStack> stacks = Lists.newArrayList();
        int maxStackSize = new ItemStack(item).getMaxStackSize();

        // return an empty array since we couldn't get the max stack size
        if (maxStackSize == 0) {
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

    public static void removeFromPlayerInventory(EntityPlayerMP player, ItemStack s, int amount) {
        Map<Integer, ItemStack> stacks = ItemHelper.getTotalStacks(player.inventory, s);
        Set<Integer> slotsToRemove = Sets.newTreeSet();
        int slotToDecrease = -1;
        int totalRemoved = amount;

        for (Integer slotID : stacks.keySet()) {
            ItemStack stack = stacks.get(slotID);

            totalRemoved -= stack.getCount();

            if (totalRemoved >= 0) {
                // remove item
                slotsToRemove.add(slotID);
            } else {
                // decrease stack size
                slotToDecrease = slotID;
                break;
            }
        }

        slotsToRemove.forEach(slot -> player.inventory.setInventorySlotContents(slot, ItemStack.EMPTY));

        if (slotToDecrease != -1) {
            ItemStack lastStack = player.inventory.getStackInSlot(slotToDecrease).copy();
            lastStack.setCount(Math.abs(totalRemoved));
            player.inventory.setInventorySlotContents(slotToDecrease, lastStack);
        }
        player.inventoryContainer.detectAndSendChanges();
    }

    public static int amountInInventory(EntityPlayerMP player, ItemStack s) {
        Map<Integer, ItemStack> stacks = ItemHelper.getTotalStacks(player.inventory, s);
        int totalCount = 0;
        for (ItemStack stack : stacks.values()) totalCount += stack.getCount();
        return totalCount;
    }

    public static Map<Enchantment, Integer> getEnchantments(ItemStack stack) {
        Map<Enchantment, Integer> enchantments = Maps.newHashMap();
        if (stack == null) return enchantments;

        NBTTagList nbttaglist = stack.getEnchantmentTagList();

        for (int j = 0; j < nbttaglist.tagCount(); ++j) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(j);
            int k = nbttagcompound.getShort("id");
            int l = nbttagcompound.getShort("lvl");
            Enchantment enchantment = Enchantment.getEnchantmentByID(k);

            if (enchantment != null) {
                enchantments.put(enchantment, l);
            }
        }

        return enchantments;
    }

    public static void removeEnchantment(ItemStack stack, Enchantment enchantment) {
        Map<Enchantment, Integer> enchantments = ItemHelper.getEnchantments(stack);
        stack.getTagCompound().setTag("ench", new NBTTagList());
        enchantments.forEach((ench, lvl) -> {
            if (ench != enchantment) stack.addEnchantment(ench, lvl);
        });
    }


}