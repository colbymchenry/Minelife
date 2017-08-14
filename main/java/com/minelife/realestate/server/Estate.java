package com.minelife.realestate.server;

import com.minelife.Minelife;
import com.minelife.region.server.Region;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Estate implements Comparable<Estate> {

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

    private static Estate getFromUUID(UUID estateUUID) throws EstateException {
        List<Estate> estates = ESTATES.stream().filter(estate -> estate.getUUID().equals(estateUUID)).collect(Collectors.toList());
        if (estates.size() != 1) throw new EstateException("More than 1 Estate with same UUID!");
        return estates.get(0);
    }

    public static void delete(UUID estateUUID) throws SQLException, EstateException {
        Estate estate = Estate.getFromUUID(estateUUID);
        Minelife.SQLITE.query("DELETE FROM estates WHERE uuid='" + estateUUID.toString() + "'");
        ESTATES.remove(estate);
    }

    private Estate(UUID estateUUID) throws SQLException {

        this.uuid = estateUUID;

        ResultSet result = Minelife.SQLITE.query("SELECT * FROM estates WHERE uuid='" + this.uuid.toString() + "'");

        Arrays.asList(result.getString("region_uuids").split(Pattern.quote("|"))).stream().map(UUID::fromString).map(Region::getRegionFromUUID).forEach(region -> this.regions.add(region));
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

    public Estate getParent() throws EstateException {

        List<Estate> parents = ESTATES.stream().filter(estate -> estate.regions.equals(this.regions.stream().map(Region::getParentRegion).collect(Collectors.toList()))).collect(Collectors.toList());
        if (parents.size() > 1) throw new EstateException("Estate (uuid = " + this.uuid + ") has more than one parent.");
        if (parents.isEmpty()) return null;
        else return parents.get(0);

    }

    public List<Estate> getChildren() throws EstateException {

        return ESTATES.stream().filter(estate -> {
            try { return estate.getParent().equals(this); }
            catch (EstateException e) { e.printStackTrace(); }
            return false;
        }).collect(Collectors.toList());

    }

    public UUID getUUID() {
        return this.uuid;
    }

    public static void initEstates() throws SQLException {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM estates");
        while (result.next()) ESTATES.add(new Estate(UUID.fromString(result.getString("uuid"))));
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Estate && ((Estate) obj).uuid.equals(this.uuid);
    }

    /**
     * Compares this Estate with the specified Estate.
     *
     * <p> The first of two Estates is greater than the second if the first
     * Estate is a parent of the second, and vice versa. Returns 0 if there
     * is no parent-child relationship between the Estates or if the Estates
     * are equivalent.
     *
     * @param  estate
     *         {@code Estate} to which this {@code Estate} is to be compared
     *
     * @return  -1, 0 or 1 as this {@code UUID} is less than, equal to, or
     *          greater than {@code val}
     *
     */
    @Override
    public int compareTo(Estate estate) {

        Estate e = estate;

        try {
            while (e.getParent() != null) {
                if (e.getParent().equals(this)) return 1;
                e = e.getParent();
            }

            e = this;

            while (e.getParent() != null) {
                if (e.getParent().equals(estate)) return -1;
                e = e.getParent();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return 0;

    }

}