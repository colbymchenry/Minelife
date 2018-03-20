package com.minelife.economy.tileentity;

import codechicken.lib.inventory.InventoryUtils;
import com.minelife.util.MLInventory;
import com.minelife.util.MLTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityCash extends MLTileEntity {

    private MLInventory inventory = new MLInventory(54, null, 64);

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("Inventory", this.inventory.writeToNBT(new NBTTagCompound()));
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.inventory.readFromNBT((NBTTagCompound) compound.getTag("Inventory"));
    }

    public MLInventory getInventory() {
        return this.inventory;
    }

    public int deposit(ItemStack stack) {
        return InventoryUtils.insertItem(this.getInventory(), stack, false);
    }
}
