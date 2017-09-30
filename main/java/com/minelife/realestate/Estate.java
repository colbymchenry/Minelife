package com.minelife.realestate;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.region.server.Region;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Estate implements Comparable<Estate> {

    public static final Set<Estate> estates = new TreeSet<>();

    private Region region;
    private Set<Member> members;
    private Set<EnumPermission> permissions;
    private double rentPrice, purchasePrice;
    private boolean forRent;
    private int rentPeriodInDays;
    private UUID owner, renter;

    public Estate(ResultSet result) throws SQLException
    {
        this.region = Region.getRegionFromUUID(UUID.fromString(result.getString("region")));

        members = new TreeSet<>();
        permissions = new TreeSet<>();
        rentPrice = result.getDouble("rentPrice");
        purchasePrice = result.getDouble("purchasePrice");
        forRent = result.getBoolean("forRent");
        rentPeriodInDays = result.getInt("rentPeriodInDays");
        owner = UUID.fromString(result.getString("owner"));
        if (!result.getString("renter").isEmpty())
            owner = UUID.fromString(result.getString("renter"));

        String[] membersArray = result.getString("members").split(".");
        for (String s : membersArray) {
            if (!s.isEmpty()) {
                Member member = Member.fromString(s);
                member.estate = this;
                members.add(member);
            }
        }

        String[] permsArray = result.getString("permissions").split(",");
        for (String s : permsArray)
            if (!s.isEmpty()) permissions.add(EnumPermission.values()[Integer.parseInt(s)]);
    }

    public Estate(Region region)
    {
        this.region = region;
    }

    public void setOwner(UUID owner) throws SQLException
    {
        this.owner = owner;
        Minelife.SQLITE.query("UPDATE estates SET owner='" + owner.toString() + "' WHERE region='" + region.getUniqueID().toString() + "'");
    }

    public void setRenter(UUID renter) throws SQLException
    {
        this.renter = renter;
        Minelife.SQLITE.query("UPDATE estates SET renter='" + renter.toString() + "' WHERE region='" + region.getUniqueID().toString() + "'");
    }

    public void setRentPeriodInDays(int days) throws SQLException
    {
        this.rentPeriodInDays = days;
        Minelife.SQLITE.query("UPDATE estates SET rentPeriodInDays='" + days + "' WHERE region='" + region.getUniqueID().toString() + "'");
    }

    public void setForRent(boolean forRent) throws SQLException
    {
        this.forRent = forRent;
        Minelife.SQLITE.query("UPDATE estates SET forRent='" + (forRent ? 1 : 0) + "' WHERE region='" + region.getUniqueID().toString() + "'");
    }

    public void setRentPrice(double rentPrice) throws SQLException
    {
        this.rentPrice = rentPrice;
        Minelife.SQLITE.query("UPDATE estates SET rentPrice='" + rentPrice + "' WHERE region='" + region.getUniqueID().toString() + "'");
    }

    public void setPurchasePrice(double purchasePrice) throws SQLException
    {
        this.purchasePrice = purchasePrice;
        Minelife.SQLITE.query("UPDATE estates SET purchasePrice='" + purchasePrice + "' WHERE region='" + region.getUniqueID().toString() + "'");
    }

    public void setPermissions(Set<EnumPermission> permissions) throws SQLException
    {
        this.permissions = permissions;
        final StringBuilder builder = new StringBuilder();
        permissions.forEach(p -> builder.append(p.ordinal()).append(","));
        Minelife.SQLITE.query("UPDATE estates SET permissions='" + builder.toString() + "' WHERE region='" + region.getUniqueID().toString() + "'");
    }

    public void setPermissionsAllowedToChange(Set<EnumPermission> permissions) throws SQLException
    {
        final StringBuilder builder = new StringBuilder();
        permissions.forEach(p -> builder.append(p.ordinal()).append(","));
        Minelife.SQLITE.query("UPDATE estates SET permsAllowedToChange='" + builder.toString() + "' WHERE region='" + region.getUniqueID().toString() + "'");
    }

    public void setMembers(Set<Member> members) throws SQLException
    {
        this.members = members;
        StringBuilder builder = new StringBuilder();
        members.forEach(m -> builder.append(m.toString()).append("."));
        Minelife.SQLITE.query("UPDATE estates SET members='" + builder.toString() + "' WHERE region='" + region.getUniqueID().toString() + "'");
    }

    public void setRegion(Region region) throws Exception
    {
        if (estates.stream().filter(e -> e.region.equals(region) && !e.equals(this)).findFirst().orElse(null) != null)
            throw new Exception("An estate is already assigned to that region.");
        Minelife.SQLITE.query("UPDATE estates SET region='" + region.getUniqueID().toString() + "' WHERE region='" + this.region.getUniqueID().toString() + "'");
        this.region = region;
    }

    public Region getRegion()
    {
        return region;
    }

    public Set<Member> getMembers()
    {
        return members;
    }

    public Set<EnumPermission> getPermissions()
    {
        return permissions;
    }

    public double getRentPrice()
    {
        return rentPrice;
    }

    public double getPurchasePrice()
    {
        return purchasePrice;
    }

    public boolean isForRent()
    {
        return forRent;
    }

    public int getRentPeriodInDays()
    {
        return rentPeriodInDays;
    }

    public UUID getOwner()
    {
        return owner;
    }

    public UUID getRenter()
    {
        return renter;
    }

    public Estate getParentEstate()
    {
        return estates.stream().filter(e -> e.region.equals(region.getParentRegion())).findFirst().orElse(null);
    }

    // Only owner of parent region can define these
    public Set<EnumPermission> getPermissionsAllowedToChange(UUID playerUUID) throws SQLException
    {
        Set<EnumPermission> permissions = new TreeSet<>();
        Map<Integer, Estate> parentEstates = getEstatesInOrder();
        Set<Integer> keySet = parentEstates.keySet();

        // check if they are the top level owner, if so they get all the permissions
        if(parentEstates.isEmpty() || parentEstates.get(keySet.toArray()[0]).getOwner().equals(owner)) permissions.addAll(Arrays.asList(EnumPermission.values()));

        // if not top level go through the estates until we reach the owner.
        for (Estate estate : parentEstates.values()) {
            if(estate.owner.equals(playerUUID)) {
                ResultSet result = Minelife.SQLITE.query("SELECT * FROM estates WHERE owner='" + playerUUID.toString() + "' AND region='" + estate.getRegion().getUniqueID().toString() + "'");
                if(result.next()) {
                    String[] data = result.getString("permsAllowedToChange").split(",");
                    for (String datum : data) {
                        if(!datum.isEmpty()) permissions.add(EnumPermission.values()[Integer.parseInt(datum)]);
                    }
                }
                break;
            }
        }

        return permissions;
    }

    public Set<EnumPermission> getPermissionsAllowedToChangeEnabled() throws SQLException
    {
        Set<EnumPermission> permissions = new TreeSet<>();
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM estates WHERE region='" + getRegion().getUniqueID().toString() + "'");
        if(result.next()) {
            String[] data = result.getString("permsAllowedToChange").split(",");
            for (String datum : data) {
                if(!datum.isEmpty()) permissions.add(EnumPermission.values()[Integer.parseInt(datum)]);
            }
        }
        return permissions;
    }

    public boolean showPermsAllowedToChange(UUID playerUUID) {
        Map<Integer, Estate> parentEstates = getEstatesInOrder();
        Set<Integer> keySet = parentEstates.keySet();

        // check if they are the top level owner, if so they get all the permissions
        if(parentEstates.isEmpty() || parentEstates.get(keySet.toArray()[0]).getOwner().equals(owner)) return true;

        // if not top level go through the estates until we reach the owner.
        for (Estate estate : parentEstates.values()) {
            if(estate.owner.equals(playerUUID)) {
                if(getRegion().contains(estate.region)) return true;
            }
        }

        return false;
    }

    public boolean hasPermission(UUID playerUUID, EnumPermission permission)
    {
        try {
            // if they are the owner or renter and can modify the permission then they have the permission
            if ((playerUUID.equals(owner) || (renter != null && renter.equals(playerUUID))) && getPermissionsAllowedToChange(playerUUID).contains(permission)) return true;

            // check member permissions
            Member member = members.stream().filter(m -> m.playerUUID.equals(playerUUID)).findFirst().orElse(null);
            if (member != null && member.permissions.contains(permission)) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // check global permission
        return permissions.contains(permission);
    }

    public Map<Integer, Estate> getEstatesInOrder()
    {
        Set<Estate> estates = new TreeSet<>();
        Estate estate = getParentEstate();

        if(estate != null) {
            while (!estates.contains(estate)) {
                estates.add(estate);
                estate = getParentEstate();
                System.out.println(estate.getRegion().getUniqueID().equals(getRegion().getUniqueID()));
            }
        }

        Map<Integer, Estate> estatesMap = new TreeMap<>();
        for (Estate e : estates) estatesMap.put((int) e.getRegion().getBounds().minX, e);
        return estatesMap;
    }

    public void toBytes(ByteBuf buf)
    {
        region.toBytes(buf);
        buf.writeInt(members.size());
        members.forEach(m -> m.toBytes(buf));
        buf.writeInt(permissions.size());
        permissions.forEach(p -> buf.writeInt(p.ordinal()));
        buf.writeDouble(rentPrice);
        buf.writeDouble(purchasePrice);
        buf.writeBoolean(forRent);
        buf.writeInt(rentPeriodInDays);
        ByteBufUtils.writeUTF8String(buf, owner.toString());
        ByteBufUtils.writeUTF8String(buf, renter == null ? "null" : renter.toString());
    }

    public static Estate fromBytes(ByteBuf buf)
    {
        Region region = Region.fromBytes(buf);
        Estate estate = new Estate(region);
        Set<Member> members = new TreeSet<>();
        int membersSize = buf.readInt();
        for (int i = 0; i < membersSize; i++) members.add(Member.fromBytes(buf));
        Set<EnumPermission> permissions = new TreeSet<>();
        int permissionsSize = buf.readInt();
        for (int i = 0; i < permissionsSize; i++) permissions.add(EnumPermission.values()[buf.readInt()]);
        double rentPrice = buf.readDouble();
        double purchasePrice = buf.readDouble();
        boolean forRent = buf.readBoolean();
        estate.rentPeriodInDays = buf.readInt();
        UUID owner = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        String renterStr = ByteBufUtils.readUTF8String(buf);
        UUID renter = renterStr.equals("null") ? null : UUID.fromString(renterStr);
        estate.region = region;
        estate.members = members;
        estate.permissions = permissions;
        estate.rentPrice = rentPrice;
        estate.purchasePrice = purchasePrice;
        estate.forRent = forRent;
        estate.owner = owner;
        estate.renter = renter;
        return estate;
    }


    @Override
    public int compareTo(Estate o)
    {
        return o.region.getUniqueID().equals(region.getUniqueID()) ? 0 : -1;
    }
}
