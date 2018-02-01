package com.minelife.economy;

import com.google.common.collect.Lists;
import com.minelife.MLBlocks;
import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.economy.cash.TileEntityCash;
import com.minelife.util.ItemHelper;
import com.minelife.util.NumberConversions;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

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
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        if (world.isRemote) return true;

        if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityCash) {
            TileEntityCash TileCash = (TileEntityCash) world.getTileEntity(x, y, z);
            int newStackSize = TileCash.addCash(stack);
            ItemStack newStack = stack.copy();
            newStack.stackSize = newStackSize;
            player.inventory.setInventorySlotContents(player.inventory.currentItem, newStack);
            return true;
        }

        switch (side) {
            case 0:
                y -= 1;
                break;
            case 1:
                y += 1;
                break;
            case 2:
                z -= 1;
                break;
            case 3:
                z += 1;
                break;
            case 4:
                x -= 1;
                break;
            case 5:
                x += 1;
                break;
        }

        world.setBlock(x, y, z, MLBlocks.cash);
        TileEntityCash tileEntityCash = (TileEntityCash) world.getTileEntity(x, y, z);
        tileEntityCash.addCash(stack);

        int l = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        tileEntityCash.setFacing(l == 0 ? EnumFacing.NORTH : l == 1 ? EnumFacing.EAST : l == 2 ? EnumFacing.SOUTH : EnumFacing.WEST);
        return true;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return "$" + NumberConversions.formatter.format(((ItemMoney) stack.getItem()).amount * stack.stackSize);
    }

    public static List<ItemStack> getDrops(double amount) {
        amount = Math.floor(amount);

        if (amount > 3456000) return null;

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
        if (thousands > 0)
            stacks.addAll(ItemHelper.getStacks(MLItems.dollar_1000, thousands));
        if (five_hundreds > 0)
            stacks.addAll(ItemHelper.getStacks(MLItems.dollar_500, five_hundreds));
        if (two_hundred_fifties > 0)
            stacks.addAll(ItemHelper.getStacks(MLItems.dollar_250, two_hundred_fifties));
        if (hundreds > 0)
            stacks.addAll(ItemHelper.getStacks(MLItems.dollar_100, hundreds));
        if (fifties > 0)
            stacks.addAll(ItemHelper.getStacks(MLItems.dollar_50, fifties));
        if (twenties > 0)
            stacks.addAll(ItemHelper.getStacks(MLItems.dollar_20, twenties));
        if (tens > 0)
            stacks.addAll(ItemHelper.getStacks(MLItems.dollar_10, tens));
        if (fives > 0)
            stacks.addAll(ItemHelper.getStacks(MLItems.dollar_5, fives));
        if (ones > 0)
            stacks.addAll(ItemHelper.getStacks(MLItems.dollar_1, ones));

        return stacks;
    }

}
