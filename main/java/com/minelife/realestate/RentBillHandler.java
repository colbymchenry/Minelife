package com.minelife.realestate;

import com.minelife.economy.BillHandler;
import com.minelife.economy.Billing;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.MoneyHandler;
import com.minelife.util.PlayerHelper;
import net.minecraft.nbt.NBTTagCompound;

public class RentBillHandler extends BillHandler {

    public int estateID;

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("estateID", estateID);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        estateID = tagCompound.getInteger("estateID");
    }

    @Override
    public void update() {
        Estate estate = EstateHandler.getEstate(estateID);
        if(estate == null) bill.delete();
    }

    @Override
    public void pay(Billing.Bill bill, int amount) {
        Estate estate = EstateHandler.getEstate(estateID);
        try {
            if (estate != null && MoneyHandler.getBalanceVault(estate.getRenter()) >= amount) {
                MoneyHandler.addMoneyVault(estate.getOwner(), amount);
                MoneyHandler.takeMoneyVault(estate.getRenter(), amount);
                bill.setAmountDue(bill.getAmountDue() - amount);
                // send notification to owner for payment
                PaymentNotification notification = new PaymentNotification(estate.getOwner(), amount, estateID, true);
                if (PlayerHelper.getPlayer(estate.getOwner()) != null)
                    notification.sendTo(PlayerHelper.getPlayer(estate.getOwner()));
                else
                    notification.writeToDB();
            } else {
                if(estate == null) bill.delete();
                else {
                    estate.setRenter(null);
                    estate.setBill(null);
                    bill.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
