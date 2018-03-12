package com.minelife.locks;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.Random;

public class TileEntityLock extends TileEntity {

    private static final Random r = new Random();

    public LockType lockType;
    public int protectX, protectY, protectZ;
    public Block protectedBlockType;

    public TileEntityLock() {
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        if (tagCompound.hasKey("lockType"))
            lockType = LockType.values()[tagCompound.getInteger("lockType")];

        if(tagCompound.hasKey("protectedBlockType"))
            protectedBlockType = Block.getBlockFromName(tagCompound.getString("protectedBlockType"));

        protectX = tagCompound.getInteger("protectX");
        protectY = tagCompound.getInteger("protectY");
        protectZ = tagCompound.getInteger("protectZ");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        if (lockType != null)
            tagCompound.setInteger("lockType", lockType.ordinal());

        if(protectedBlockType != null)
            tagCompound.setString("protectedBlockType", protectedBlockType.getUnlocalizedName());

        tagCompound.setInteger("protectX", protectX);
        tagCompound.setInteger("protectY", protectY);
        tagCompound.setInteger("protectZ", protectZ);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }

    public void Sync() {
        this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        this.markDirty();
    }

    public boolean attemptUnlock() {
        int n = r.nextInt(1000);
        return n >= lockType.min && n <= lockType.max;
    }
}
