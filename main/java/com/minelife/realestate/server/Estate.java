package com.minelife.realestate.server;

import com.minelife.Minelife;
import com.minelife.region.server.Region;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Estate {

    public static final Set<Estate> ESTATES = new TreeSet<>();

    private List<Region> regions = new ArrayList<>();
    private UUID uuid;
    private UUID owner_uuid;

    public static Estate create(Region region, UUID owner) throws SQLException {

        UUID estateUUID = UUID.randomUUID();

        // Create Estate
        Minelife.SQLITE.query("INSERT INTO estates (uuid, owner_uuid, region_uuids) VALUES (" +
                "'" + estateUUID + "'," +
                "'" + owner + "'," +
                "'" + region.getUniqueID() + "')");

        Estate estate = new Estate(estateUUID);

        ESTATES.add(estate);

        return estate;

    }

    private Estate(UUID estateUUID) throws SQLException {

        this.uuid = estateUUID;

        ResultSet result = Minelife.SQLITE.query("SELECT * FROM estates WHERE uuid='" + this.uuid.toString() + "'");

        this.regions.add(Region.getRegionFromUUID(UUID.fromString(result.getString("region_uuid"))));
        this.owner_uuid = UUID.fromString(result.getString("owner_uuid"));

    }

    public boolean add(Region region) throws SQLException {
        boolean b = this.regions.add(region);
        // Update Table
        Minelife.SQLITE.query("UPDATE estates " +
                "SET region_uuids = " +
                "'" + this.regions.stream().map(r -> r.getUniqueID().toString()).reduce("", (a, r) -> a + "|" + r) + "' " +
                "WHERE uuid = '" + this.uuid.toString() + "'");
        return b;
    }

    private Estate getParent() throws EstateException {

        List<Estate> parents = ESTATES.stream().filter(estate -> estate.regions.equals(this.regions.stream().map(Region::getParentRegion).collect(Collectors.toList()))).collect(Collectors.toList());
        if (parents.size() != 1) throw new EstateException("Estate (uuid = " + this.uuid + ") has more than one parent.");
        return parents.get(0);

    }

    private List<Estate> getChildren() throws EstateException {

        return ESTATES.stream().filter(estate -> {
            try { return estate.getParent().equals(this); }
            catch (EstateException e) { e.printStackTrace(); }
            return false;
        }).collect(Collectors.toList());

    }

    public static void initEstates() throws SQLException {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM estates");
        while (result.next()) ESTATES.add(new Estate(UUID.fromString(result.getString("uuid"))));
    }

}