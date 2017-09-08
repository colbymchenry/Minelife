package com.minelife.police;

import buildcraft.core.lib.inventory.SimpleInventory;
import com.google.common.collect.Lists;
import com.minelife.MLItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class TicketInventory implements IInventory {

    private final SimpleInventory inv;
    private final ItemStack ticketStack;
    private final int slot;

    public TicketInventory(ItemStack ticketStack, int slot) {
        inv = new SimpleInventory(9, "ticketInventory", 64);
        this.ticketStack = ticketStack;
        this.slot = slot;
        if (ticketStack.stackTagCompound.hasKey("items"))
            inv.readFromNBT(ticketStack.stackTagCompound.getCompoundTag("items"));
    }

    @Override
    public int getSizeInventory() {
        return inv.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int p_70301_1_) {
        return inv.getStackInSlot(p_70301_1_);
    }

    @Override
    public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
        return inv.decrStackSize(p_70298_1_, p_70298_2_);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
        return inv.getStackInSlotOnClosing(p_70304_1_);
    }

    @Override
    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
        inv.setInventorySlotContents(p_70299_1_, p_70299_2_);
    }

    @Override
    public String getInventoryName() {
        return inv.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return inv.getInventoryStackLimit();
    }

    @Override
    public void markDirty() {
        inv.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
        return true;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
        NBTTagCompound invTag = new NBTTagCompound();
        inv.writeToNBT(invTag);
        ticketStack.stackTagCompound.setTag("items", invTag);
    }

    public void updateCreative(EntityPlayerMP player) {
        player.inventory.setInventorySlotContents(slot, ticketStack);
    }

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
        return true;
    }

}
