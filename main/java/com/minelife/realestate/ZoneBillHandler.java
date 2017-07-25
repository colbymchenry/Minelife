package com.minelife.realestate;

import com.minelife.economy.BillHandler;
import com.minelife.economy.Billing;
import com.minelife.economy.ModEconomy;
import com.minelife.realestate.sign.TileEntityForSaleSign;
import com.minelife.region.server.Region;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import java.util.UUID;

public class ZoneBillHandler extends BillHandler {

    private UUID zoneUUID;
    private UUID forSaleSignUUID;

    public ZoneBillHandler()
    {
    }

    public ZoneBillHandler(UUID zone, UUID forSaleSignUUID)
    {
        this.zoneUUID = zone;
        this.forSaleSignUUID = forSaleSignUUID;
        writeToNBT(tagCompound);
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setString("zoneUUID", zoneUUID.toString());
        tagCompound.setString("forSaleSignUUID", forSaleSignUUID.toString());
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        this.zoneUUID = UUID.fromString(tagCompound.getString("zoneUUID"));
        this.forSaleSignUUID = UUID.fromString(tagCompound.getString("forSaleSignUUID"));
    }

    @Override
    public void update()
    {
        if (this.zoneUUID == null) {
            Billing.deleteBill(this.bill.getUniqueID());
        } else {
            if (getZone() == null) {
                Billing.deleteBill(this.bill.getUniqueID());
            } else {
                if (!getZone().hasForSaleSign(Side.SERVER)) {
                    Billing.deleteBill(this.bill.getUniqueID());
                } else {
                    TileEntityForSaleSign forSaleSign = getZone().getForSaleSign(Side.SERVER);
                    if (!forSaleSign.getUniqueID().equals(this.forSaleSignUUID)) {
                        Billing.deleteBill(this.bill.getUniqueID());
                    } else if (!forSaleSign.getRenter().equals(bill.getPlayer())) {
                        Billing.deleteBill(this.bill.getUniqueID());
                    }
                }
            }
        }
    }

    @Override
    public void pay(Billing.Bill bill, long amount)
    {
        try {
            if (ModEconomy.getBalance(bill.getPlayer(), false) >= amount) {
                ModEconomy.withdraw(bill.getPlayer(), amount, false);
                ModEconomy.deposit(getZone().getOwner(), amount, false);
                Zone zone = Zone.getZone(Region.getRegionFromUUID(zoneUUID));
                RentPaidNotification rentPaidNotification = new RentPaidNotification(bill.getPlayer(),
                        "Rent for Zone", EnumChatFormatting.RED + "-$" + NumberConversions.formatter.format(amount), zoneUUID,
                        (int) zone.getRegion().getBounds().minX,
                        (int) zone.getRegion().getBounds().minY,
                        (int) zone.getRegion().getBounds().minZ);
                if (PlayerHelper.getPlayer(bill.getPlayer()) != null) {
                    rentPaidNotification.sendTo(PlayerHelper.getPlayer(bill.getPlayer()));
                } else {
                    rentPaidNotification.writeToDB();
                }

                rentPaidNotification = new RentPaidNotification(getZone().getOwner(),
                        "Rent for Zone", EnumChatFormatting.GREEN + "+$" + NumberConversions.formatter.format(amount), zoneUUID,
                        (int) zone.getRegion().getBounds().minX,
                        (int) zone.getRegion().getBounds().minY,
                        (int) zone.getRegion().getBounds().minZ);

                if (PlayerHelper.getPlayer(getZone().getOwner()) != null) {
                    rentPaidNotification.sendTo(PlayerHelper.getPlayer(getZone().getOwner()));
                } else {
                    rentPaidNotification.writeToDB();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete()
    {
        // TODO: Give money back to player if in negative
    }

    public Zone getZone()
    {
        return Zone.getZone(Region.getRegionFromUUID(this.zoneUUID));
    }
}
