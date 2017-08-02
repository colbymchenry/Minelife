package com.minelife.drug.tileentity;

import buildcraft.BuildCraftCore;
import buildcraft.api.core.ISerializable;
import buildcraft.core.DefaultProps;
import buildcraft.core.lib.inventory.SimpleInventory;
import buildcraft.core.lib.network.PacketTileUpdate;
import com.minelife.drug.item.ItemCocaLeaf;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityDryingRack extends TileEntity implements IInventory, ISerializable {

    private SimpleInventory inv;
    private int progress;

    public TileEntityDryingRack()
    {
        inv = new SimpleInventory(9, "drying_rack", 1);
    }

    @Override
    public void updateEntity()
    {
        progress += 1;
        if(progress >= 256) {
            progress = 0;
            for(int i = 0; i < 9; i++) {
                ItemCocaLeaf.set_moisture_level(getStackInSlot(i), ItemCocaLeaf.get_moisture_level(getStackInSlot(i)) - 1);
            }
            // TODO: Not really sure what sendNetworkUpdate does, need to find out
            sendNetworkUpdate();
        }
    }

    public void sendNetworkUpdate() {
        if (this.worldObj != null && !this.worldObj.isRemote) {
            BuildCraftCore.instance.sendToPlayers(new PacketTileUpdate(this), this.worldObj, this.xCoord, this.yCoord, this.zCoord, DefaultProps.NETWORK_UPDATE_RANGE);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        inv.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        inv.readFromNBT(tag);
    }

    @Override
    public int getSizeInventory()
    {
        return inv.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return inv.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        return inv.decrStackSize(slot, amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        return inv.getStackInSlotOnClosing(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        inv.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getInventoryName()
    {
        return inv.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return inv.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return stack != null && stack.getItem() == ItemCocaLeaf.instance() && ItemCocaLeaf.get_moisture_level(stack) == 0;
    }

    @Override
    public void writeData(ByteBuf byteBuf)
    {

    }

    @Override
    public void readData(ByteBuf byteBuf)
    {

    }
}
