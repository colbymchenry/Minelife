package com.minelife.realestate;

import com.minelife.economy.BillHandler;
import com.minelife.economy.Billing;
import com.minelife.economy.ModEconomy;
import com.minelife.region.server.Region;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class ZoneBillHandler extends BillHandler {

    private UUID zoneUUID;

    public ZoneBillHandler() {}

    public ZoneBillHandler(UUID zone) {
        this.zoneUUID = zone;
        writeToNBT(tagCompound);
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setString("zoneUUID", zoneUUID.toString());
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        this.zoneUUID = UUID.fromString(tagCompound.getString("zoneUUID"));
    }

    @Override
    public void update()
    {
        if(this.zoneUUID == null) {
            Billing.deleteBill(this.bill.getUniqueID());
        } else {
            if(getZone() == null) {
                Billing.deleteBill(this.bill.getUniqueID());
            } else {
                if(!getZone().hasForSaleSign(Side.SERVER)) {
                    Billing.deleteBill(this.bill.getUniqueID());
                }
            }
        }
    }

    @Override
    public void pay(Billing.Bill bill, long amount)
    {
        try {
            ModEconomy.withdraw(bill.getPlayer(), amount, false);
            ModEconomy.deposit(getZone().getOwner(), amount, false);
            // TODO: Send RentPaidNotification: Finish it
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete()
    {

    }

    public Zone getZone() {
        return Zone.getZone(Region.getRegionFromUUID(this.zoneUUID));
    }
}
