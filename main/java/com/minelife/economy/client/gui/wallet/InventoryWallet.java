package com.minelife.economy.client.gui.wallet;

import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.inventory.InventoryUtils;
import com.google.common.collect.Lists;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.WithdrawlResult;
import com.minelife.economy.item.ItemCash;
import com.minelife.util.MLInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class InventoryWallet {

    private ItemStack walletStack;
    private MLInventory inventory = new MLInventory(54, null, 64);

    public InventoryWallet(ItemStack walletStack) {
        this.walletStack = walletStack;
        this.readFromNBT();
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = walletStack.hasTagCompound() ? walletStack.getTagCompound() : new NBTTagCompound();
        compound.setTag("Inventory", this.inventory.writeToNBT(new NBTTagCompound()));
        this.walletStack.setTagCompound(compound);
        return compound;
    }

    public void readFromNBT() {
        NBTTagCompound compound = walletStack.hasTagCompound() ? walletStack.getTagCompound() : new NBTTagCompound();
        if (compound.hasKey("Inventory"))
            this.inventory.readFromNBT((NBTTagCompound) compound.getTag("Inventory"));
    }

    public MLInventory getInventory() {
        return this.inventory;
    }

    public ItemStack getWalletStack() {
        return walletStack;
    }

    public int deposit(ItemStack stack) {
        return InventoryUtils.insertItem(this.getInventory(), stack, false);
    }

    public int deposit(int amount) {
        return ModEconomy.deposit(new InventoryRange(this.getInventory(), 0, this.getInventory().getSizeInventory()), amount);
    }

    public WithdrawlResult withdraw(int amount) {
        return ModEconomy.withdraw(new InventoryRange(this.getInventory(), 0, this.getInventory().getSizeInventory()), amount);
    }

    public int getBalance() {
        int total = 0;
        for (ItemStack itemStack : inventory.getItems()) {
            if (itemStack.getItem() == ModEconomy.itemCash)
                total += ItemCash.getAmount(itemStack);
        }
        return total;
    }

}
