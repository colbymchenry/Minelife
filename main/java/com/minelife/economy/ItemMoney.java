package com.minelife.economy;

import com.google.common.collect.Lists;
import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.util.ItemHelper;
import com.minelife.util.NumberConversions;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.List;

public class ItemMoney extends Item {

    public final int amount;

    public ItemMoney(int amount) {
        this.amount = amount;
        setCreativeTab(CreativeTabs.tabMisc);
        setUnlocalizedName("money_" + amount);
        setTextureName(Minelife.MOD_ID + ":dollar_" + amount);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return "$" + NumberConversions.formatter.format(((ItemMoney) stack.getItem()).amount * stack.stackSize);
    }

    public static List<ItemStack> getDrops(double amount) {
        amount = Math.floor(amount);

        if (amount > 2304000) return null;

        int thousands = (int) Math.floor(amount / 1000);
        amount -= (thousands * 1000);
        int five_hundreds = (int) Math.floor(amount / 500);
        amount -= (five_hundreds * 500);
        int two_hundred_fifties = (int) Math.floor(amount / 250);
        amount -= (two_hundred_fifties * 250);
        int hundreds = (int) Math.floor(amount / 100);
        amount -= (hundreds * 100);
        int fifties = (int) Math.floor(amount / 50);
        amount -= (fifties * 50);
        int twenties = (int) Math.floor(amount / 20);
        amount -= (twenties * 20);
        int tens = (int) Math.floor(amount / 10);
        amount -= (tens * 10);
        int fives = (int) Math.floor(amount / 5);
        amount -= (fives * 5);
        int ones = (int) Math.floor(amount / 1);
        amount -= (ones * 1);

        List<ItemStack> stacks = Lists.newArrayList();
        stacks.addAll(ItemHelper.getStacks(MLItems.dollar_1000, thousands));
        stacks.addAll(ItemHelper.getStacks(MLItems.dollar_500, five_hundreds));
        stacks.addAll(ItemHelper.getStacks(MLItems.dollar_250, two_hundred_fifties));
        stacks.addAll(ItemHelper.getStacks(MLItems.dollar_100, hundreds));
        stacks.addAll(ItemHelper.getStacks(MLItems.dollar_50, fifties));
        stacks.addAll(ItemHelper.getStacks(MLItems.dollar_20, twenties));
        stacks.addAll(ItemHelper.getStacks(MLItems.dollar_10, tens));
        stacks.addAll(ItemHelper.getStacks(MLItems.dollar_5, fives));
        stacks.addAll(ItemHelper.getStacks(MLItems.dollar_1, ones));

        return stacks;
    }

}
