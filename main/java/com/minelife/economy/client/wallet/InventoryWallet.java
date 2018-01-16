package com.minelife.economy.client.wallet;

import buildcraft.core.lib.inventory.SimpleInventory;
import com.minelife.economy.ItemMoney;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InventoryWallet extends SimpleInventory {

    public static final int SIZE = 54;

    public ItemStack WalletStack;

    public InventoryWallet(ItemStack WalletStack) {
        super(SIZE, "wallet", 64);
        this.WalletStack = WalletStack;

        if(!WalletStack.hasTagCompound()) {
            WalletStack.setTagCompound(new NBTTagCompound());
        }

        readFromNBT(WalletStack.getTagCompound());
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        writeToNBT(data, "Items");
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        readFromNBT(data, "Items");
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return itemstack.getItem() instanceof ItemMoney;
    }

    @Override
    public void markDirty() {
        writeToNBT(WalletStack.getTagCompound());
    }

}
