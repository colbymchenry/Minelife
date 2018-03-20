package com.minelife.economy.client.gui.wallet;

import com.minelife.util.MLInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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
}
