package com.minelife.realestate;

import com.google.common.collect.Sets;
import com.minelife.economy.Bill;
import com.minelife.economy.ModEconomy;
import net.minecraft.nbt.NBTTagCompound;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class BillHandler {

    public static Set<Bill> getRentBills(UUID playerID) {
        Set<Bill> bills = Sets.newTreeSet();
        try {
            for (Bill bill : ModEconomy.getBills(playerID)) {
                if(bill.getTagCompound().hasKey("EstateID")) bills.add(bill);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bills;
    }

    public static Bill createRentBill(UUID playerID, Estate estate) throws SQLException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, estate.getRentPeriod() * 20);
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setInteger("Dimension", estate.getWorld().provider.getDimension());
        tagCompound.setIntArray("Minimum", new int[]{estate.getMinimum().getX(), estate.getMinimum().getY(), estate.getMinimum().getZ()});
        tagCompound.setIntArray("Maximum", new int[]{estate.getMaximum().getX(), estate.getMaximum().getY(), estate.getMaximum().getZ()});
        tagCompound.setString("EstateID", estate.getUniqueID().toString());
        Bill bill = new Bill(UUID.randomUUID(), playerID, "Estate Rent: x=" + estate.getMinimum().getX() +
                ",y=" + estate.getMinimum().getY() + ",z=" + estate.getMinimum().getZ(), estate.getRentPrice(), calendar.getTime(), tagCompound);
        bill.save();
        return bill;
    }

}
