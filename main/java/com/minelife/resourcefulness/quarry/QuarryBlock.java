package com.minelife.resourcefulness.quarry;

import com.minelife.util.NBTHelper;
import com.minelife.util.NumberConversions;
import com.minelife.util.irds.IRDSObject;
import com.minelife.util.irds.IRDSValue;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

public class QuarryBlock implements IRDSObject, IRDSValue<IBlockState> {

    private IBlockState block;
    private double probability;
    private boolean unique, always, enabled;

    public QuarryBlock(IBlockState block, double probability, boolean unique, boolean always, boolean enabled) {
        this.block = block;
        this.probability = probability;
        this.unique = unique;
        this.always = always;
        this.enabled = enabled;
    }

    @Override
    public double getProbability() {
        return probability;
    }

    @Override
    public boolean isUnique() {
        return unique;
    }

    @Override
    public boolean dropsAlways() {
        return always;
    }

    @Override
    public boolean canDrop() {
        return enabled;
    }

    @Override
    public void preResultEvaluation() {

    }

    @Override
    public void onHit() {

    }

    @Override
    public void postResultEvaluation() {

    }

    @Override
    public IBlockState getValue() {
        return block;
    }

    @Override
    public String toString() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        NBTUtil.writeBlockState(tagCompound, getValue());
        return probability + ";" + unique + ";" + always + ";" + enabled + ";" + tagCompound.toString();
    }

    public static QuarryBlock fromString(String str) {
        if(!str.contains(";")) return null;
        String[] data = str.split(";");
        if(data.length != 5) return null;
        double probability = NumberConversions.toDouble(data[0]);
        boolean unique = Boolean.valueOf(data[1]);
        boolean always = Boolean.valueOf(data[2]);
        boolean enabled = Boolean.valueOf(data[3]);
        IBlockState block = NBTUtil.readBlockState(NBTHelper.fromString(data[4]));
        return new QuarryBlock(block, probability, unique, always, enabled);
    }
}
