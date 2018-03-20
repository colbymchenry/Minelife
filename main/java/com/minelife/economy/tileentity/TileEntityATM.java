package com.minelife.economy.tileentity;

import com.minelife.util.MLTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntityATM extends MLTileEntity {

    private EnumFacing facing = EnumFacing.NORTH;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("Facing", this.facing.ordinal());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if(compound.hasKey("Facing")) this.facing = EnumFacing.values()[compound.getInteger("Facing")];
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }
}
