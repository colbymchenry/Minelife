package com.minelife.airdrop;

import com.minelife.util.ItemHelper;
import com.minelife.util.NumberConversions;
import com.minelife.util.irds.IRDSObject;
import com.minelife.util.irds.IRDSValue;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class ItemStackDropable implements IRDSObject, IRDSValue<ItemStack> {

    private static Random random = new Random();

    private ItemStack stack;
    private int minSize, maxSize;
    private double probability;
    private boolean unique, always, enabled;

    public ItemStackDropable(ItemStack stack, double probability, boolean unique, boolean always, boolean enabled, int minSize, int maxSize) {
        this.stack = stack;
        this.probability = probability;
        this.unique = unique;
        this.always = always;
        this.enabled = enabled;
        this.minSize = minSize;
        this.maxSize = maxSize;
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
    public ItemStack getValue() {
        return stack.copy();
    }

    @Override
    public String toString() {
        return minSize + ";" + maxSize + ";" + probability + ";" + unique + ";" + always + ";" + enabled + ";" + ItemHelper.itemToString(getValue());
    }

    public int getMinSize() {
        return minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public static ItemStackDropable fromString(String str) {
        if(!str.contains(";")) return null;
        String[] data = str.split(";");
        if(data.length != 7) return null;
        int minSize = NumberConversions.toInt(data[0]);
        int maxSize = NumberConversions.toInt(data[1]);
        double probability = NumberConversions.toDouble(data[2]);
        boolean unique = Boolean.valueOf(data[3]);
        boolean always = Boolean.valueOf(data[4]);
        boolean enabled = Boolean.valueOf(data[5]);
        ItemStack stack = ItemHelper.itemFromString(data[6]);
        return new ItemStackDropable(stack, probability, unique, always, enabled , minSize, maxSize);
    }
}
