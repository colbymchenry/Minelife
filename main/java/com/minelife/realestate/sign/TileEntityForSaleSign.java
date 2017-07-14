package com.minelife.realestate.sign;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityForSaleSign extends TileEntity {

    private boolean occupied;
    private boolean rentable;
    private long price;
    private long billingPeriod;

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);
        tagCompound.setBoolean("occupied", occupied);
        tagCompound.setBoolean("rentable", rentable);
        tagCompound.setLong("price", price);
        tagCompound.setLong("billingPeriod", billingPeriod);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);
        occupied = tagCompound.getBoolean("occupied");
        rentable = tagCompound.getBoolean("rentable");
        price = tagCompound.getLong("price");
        billingPeriod = tagCompound.getLong("billingPeriod");
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        super.onDataPacket(net, pkt);
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tagCompound);
    }

    public void setOccupied(boolean occupied)
    {
        this.occupied = occupied;
    }

    public void setRentable(boolean rentable)
    {
        this.rentable = rentable;
        sync();
    }

    public void setBillingPeriod(long billingPeriod)
    {
        this.billingPeriod = billingPeriod;
        sync();
    }

    public void setPrice(long price)
    {
        this.price = price;
        sync();
    }

    public boolean isOccupied()
    {
        return occupied;
    }

    public boolean isRentable()
    {
        return rentable;
    }

    public long getPrice()
    {
        return price;
    }

    public long getBillingPeriod()
    {
        return billingPeriod;
    }

    public void sync() {
        this.markDirty();
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
