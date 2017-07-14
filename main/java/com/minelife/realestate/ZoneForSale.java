package com.minelife.realestate;

import com.minelife.Minelife;
import io.netty.buffer.ByteBuf;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ZoneForSale {

    private Zone zone;
    public boolean forRent;
    public int daysToPay;
    public boolean breaking, placing, interacting;
    public boolean addMembers;

    private ZoneForSale()
    {
    }

    public ZoneForSale(Zone zone) throws SQLException
    {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM RealEstate_ForSale WHERE zone='" + zone.getRegion().getUniqueID().toString() + "'");
        forRent = result.getBoolean("forRent");
        daysToPay = result.getInt("daysToPay");
        breaking = result.getBoolean("breaking");
        placing = result.getBoolean("placing");
        interacting = result.getBoolean("interacting");
        addMembers = result.getBoolean("addMembers");
    }

    public ZoneForSale(Zone zone, boolean forRent, int daysToPay, boolean breaking, boolean placing, boolean interacting, boolean addMembers)
    {
        this.zone = zone;
        this.forRent = forRent;
        this.daysToPay = daysToPay;
        this.breaking = breaking;
        this.placing = placing;
        this.interacting = interacting;
        this.addMembers = addMembers;
    }

    public void save() throws SQLException
    {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM RealEstate_ForSale WHERE zone='" + zone.getRegion().getUniqueID().toString() + "'");

        if (result.next()) {
            Minelife.SQLITE.query("UPDATE RealEstate_ForSale SET " +
                    "forRent='" + (forRent ? 1 : 0) + "', " +
                    "daysToPay='" + daysToPay + "', " +
                    "breaking='" + (breaking ? 1 : 0) + "', " +
                    "placing='" + (placing ? 1 : 0) + "', " +
                    "interacting='" + (interacting ? 1 : 0) + "', " +
                    "addMembers='" + (addMembers ? 1 : 0) + "' " +
                    "WHERE zone='" + zone.getRegion().getUniqueID().toString() + "'");
        } else {
            Minelife.SQLITE.query("INSERT INTO RealEstate_ForSale (zone, forRent, daysToPay, breaking, placing, interacting, addMembers) VALUES " +
                    "('" + zone.getRegion().getUniqueID().toString() + "', '" + (forRent ? 1 : 0) + "', " +
                    "'" + daysToPay + "', '" + (breaking ? 1 : 0) + "', '" + (placing ? 1 : 0) + "', " +
                    "'" + (interacting ? 1 : 0) + "', '" + (addMembers ? 1 : 0) + "')");
        }
    }

    public static boolean hasListing(Zone zone) {
        ZoneForSale zoneForSale;
        try {
            zoneForSale = new ZoneForSale(zone);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public static ZoneForSale fromBytes(ByteBuf buf)
    {
        Zone zone = Zone.fromBytes(buf);
        ZoneForSale zoneForSale = new ZoneForSale();
        zoneForSale.zone = zone;
        zoneForSale.forRent = buf.readBoolean();
        zoneForSale.daysToPay = buf.readInt();
        zoneForSale.breaking = buf.readBoolean();
        zoneForSale.placing = buf.readBoolean();
        zoneForSale.interacting = buf.readBoolean();
        zoneForSale.addMembers = buf.readBoolean();
        return zoneForSale;
    }

    public void toBytes(ByteBuf buf)
    {
        zone.toBytes(buf);
        buf.writeBoolean(forRent);
        buf.writeInt(daysToPay);
        buf.writeBoolean(breaking);
        buf.writeBoolean(placing);
        buf.writeBoolean(interacting);
        buf.writeBoolean(addMembers);
    }

}
