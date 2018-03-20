package com.minelife.chestshop;

import com.minelife.util.MLTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.util.UUID;

public class TileEntityChestShop extends MLTileEntity {

    private UUID owner;
    private EnumFacing facing = EnumFacing.NORTH;
    private ItemStack item;
    private int price = 0;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.facing = EnumFacing.values()[tag.getInteger("Facing")];
        this.price = tag.getInteger("Price");
        if (tag.hasKey("Item")) this.item = new ItemStack(tag.getCompoundTag("Item"));
        else this.item = null;
        if (tag.hasKey("Owner")) this.owner = UUID.fromString(tag.getString("Owner"));
        else this.owner = null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Facing", this.facing.ordinal());
        tag.setInteger("Price", this.price);

        if(this.item != null){
            NBTTagCompound itemTag = new NBTTagCompound();
            this.item.writeToNBT(itemTag);
            tag.setTag("Item", itemTag);
        }
        if (this.owner != null) tag.setString("Owner", this.owner.toString());
        return tag;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}
