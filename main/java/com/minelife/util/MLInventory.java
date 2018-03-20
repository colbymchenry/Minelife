package com.minelife.util;

import com.google.common.collect.Lists;
import com.sun.istack.internal.NotNull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

public class MLInventory implements IInventory {

    private NonNullList<ItemStack> contents;

    private int size, maxStackSize;
    private String name;

    public MLInventory(int size, String name, int maxStackSize) {
        this.contents = NonNullList.withSize(size, ItemStack.EMPTY);
        this.size = size;
        this.maxStackSize = maxStackSize;
        this.name = name;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.contents) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean hasCustomName() {
        return this.name != null;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(this.name != null ? this.name : "");
    }

    @Override
    public int getSizeInventory() {
        return this.size;
    }

    @Override
    public int getInventoryStackLimit() {
        return this.maxStackSize;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        contents.clear();
    }


    public List<ItemStack> getItems() {
        List<ItemStack> items = Lists.newArrayList();
        for (int i = 0; i < getSizeInventory(); i++) {
            if (getStackInSlot(i).getItem() != Items.AIR)
                items.add(getStackInSlot(i));
        }
        return items;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.contents.set(slot, stack);
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.contents.get(slot);
    }

    @NotNull
    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = contents.get(index);
        ItemStack stack1 = stack.copy();
        stack.setCount(stack.getCount() - count < 0 ? 0 : stack.getCount() - count);
        contents.set(index, stack);
        return stack1;
    }

    @NotNull
    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack before = contents.get(index).copy();
        contents.add(index, ItemStack.EMPTY);
        return before;
    }

    public void readFromNBT(NBTTagCompound compound) {
        this.contents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.contents);
        if (compound.hasKey("Name", 8)) this.name = compound.getString("Name");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        ItemStackHelper.saveAllItems(compound, this.contents);
        if (this.hasCustomName()) compound.setString("Name", this.name);
        return compound;
    }

}
