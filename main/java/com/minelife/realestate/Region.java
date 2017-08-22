package com.minelife.realestate;

import com.minelife.Minelife;
import com.minelife.realestate.client.Selection;
import net.minecraft.util.AxisAlignedBB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

public class Region implements Comparable<Region> {

    private static final Set<Region> REGIONS = new TreeSet<>();

    private AxisAlignedBB bounds;
    private UUID uniqueID;
    private String worldName;

    private Region() { }

    private Region(UUID uniqueID) throws SQLException {
        this.uniqueID = uniqueID;
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM estate_regions WHERE uuid = '" + this.uniqueID + "')");
        this.worldName = result.getString("world");
        int i = 2;
        this.bounds = AxisAlignedBB.getBoundingBox(result.getInt(i++), result.getInt(i++), result.getInt(i++), result.getInt(i++), result.getInt(i++), result.getInt(i));
    }

    public static Region create(Selection selection) throws SQLException {
        UUID uniqueID = UUID.randomUUID();
        Minelife.SQLITE.query("INSERT INTO estate_regions (uuid, world, minX, minY, minZ, maxX, maxY, maxZ) VALUES (" +
                "'" + uniqueID.toString() + "'," +
                "'" + selection.getWorldName() + "'," +
                "'" + (int) selection.getBounds().minX + "', '" + (int) selection.getBounds().minY + "', '" + (int) selection.getBounds().minZ + "'," +
                "'" + (int) selection.getBounds().maxX + "', '" +  (int) selection.getBounds().maxY  + "', '" + (int) selection.getBounds().maxZ + "')");
        return new Region(uniqueID);
    }

    public static void delete(UUID uniqueID) throws Exception {
        List<Region> regions = REGIONS.stream().filter(r -> r.uniqueID.equals(uniqueID)).collect(Collectors.toList());
        if (regions.size() == 1) {
            Region region = regions.get(0);
            Minelife.SQLITE.query("DELETE * FROM estate_regions WHERE uuid = '" + region.uniqueID.toString() + "'");
            REGIONS.remove(region);
        } else if (regions.size() < 1) {
            throw new Exception("Region with UUID=" + uniqueID + " does not exist.");
        } else {
            throw new Exception("Too many regions with UUID=" + uniqueID + ".");
        }
    }

    public void delete() throws Exception {
        delete(this.uniqueID);
    }

    // Intersecting

    // Parent

    // Children

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Region && ((Region) obj).worldName.equals(this.worldName) && ((Region) obj).uniqueID.equals(this.uniqueID) && ((Region) obj).hasEquivalentBoundsWith(this);
    }

    private boolean hasEquivalentBoundsWith(Region region) {
        return this.bounds.minX == region.bounds.minX &&
                this.bounds.minY == region.bounds.minY &&
                this.bounds.minZ == region.bounds.minZ &&
                this.bounds.maxX == region.bounds.maxX &&
                this.bounds.maxY == region.bounds.maxY &&
                this.bounds.maxZ== region.bounds.maxZ;
    }

    @Override
    public int compareTo(Region other) {
        throw new Error("Method Not Implemented!");
//        if (other != null) {
//            if (other.getParent().equals(this)) return 1;
//            else if (this.getParent().equals(other)) return -1;
//        }
//        return 0;
    }

    public static void initRegions() throws SQLException {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM estate_regions");
        while (result.next()) REGIONS.add(new Region(UUID.fromString(result.getString("uuid"))));
    }

}