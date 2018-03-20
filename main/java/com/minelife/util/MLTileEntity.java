package com.minelife.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class MLTileEntity extends TileEntity {

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.getPos(), 3, this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        this.handleUpdateTag(pkt.getNbtCompound());
    }


    private IBlockState getState() {
        return this.getWorld().getBlockState(this.getPos());
    }

    public void sendUpdates() {
        this.getWorld().markBlockRangeForRenderUpdate(this.getPos(), this.getPos());
        this.getWorld().notifyBlockUpdate(this.getPos(), this.getState(), this.getState(), 3);
        this.getWorld().scheduleBlockUpdate(this.getPos(), this.getBlockType(), 0, 0);
        this.markDirty();
    }

}
