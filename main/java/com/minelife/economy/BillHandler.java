package com.minelife.economy;

import net.minecraft.nbt.NBTTagCompound;

import java.text.DecimalFormat;

// TODO: Let players pay bills with their vaults, atm, and money in inventory.
public abstract class BillHandler {

    public NBTTagCompound tagCompound = new NBTTagCompound();

    public Billing.Bill bill;

    public abstract void writeToNBT(NBTTagCompound tagCompound);

    public abstract void readFromNBT(NBTTagCompound tagCompound);

    public abstract void update();

    public abstract void pay(Billing.Bill bill, int amount);

}
