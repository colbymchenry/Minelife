package com.minelife.drug.tileentity;

import buildcraft.api.core.ISerializable;
import buildcraft.core.lib.inventory.SimpleInventory;
import com.google.common.collect.Maps;
import com.minelife.MLItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.Map;

public class TileEntityDryingRack extends TileEntity implements IInventory, ISerializable {

    private SimpleInventory inv;
    private int progress;
    private int[] slots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};

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
                MLItems.coca_leaf.set_moisture_level(getStackInSlot(i), MLItems.coca_leaf.get_moisture_level(getStackInSlot(i)) - 1);
            }
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
        return stack != null && stack.getItem() == MLItems.coca_leaf && MLItems.coca_leaf.get_moisture_level(stack) == 0;
    }

    @Override
    public void writeData(ByteBuf byteBuf)
    {

    }

    @Override
    public void readData(ByteBuf byteBuf)
    {

    }

    public Map<Integer, ItemStack> get_leaves() {
        Map<Integer, ItemStack> leaves = Maps.newHashMap();
        for (int i = 0; i < slots.length; i++) {
            leaves.put(i, getStackInSlot(i));
        }
        return leaves;
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        this.readFromNBT(pkt.func_148857_g());
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tagCompound = new NBTTagCompound();
        this.writeToNBT(tagCompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, blockMetadata, tagCompound);
    }
}
