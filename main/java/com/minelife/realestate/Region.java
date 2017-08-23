package com.minelife.realestate;

import com.minelife.Minelife;
import com.minelife.realestate.client.Selection;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

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
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM plots WHERE uuid = '" + this.uniqueID + "'");
        this.worldName = result.getString("world");
        int i = 2;
        this.bounds = AxisAlignedBB.getBoundingBox(result.getInt(i++), result.getInt(i++), result.getInt(i++), result.getInt(i++), result.getInt(i++), result.getInt(i));
        REGIONS.add(this);
        System.out.println("Region with UUID=" + this.uniqueID + " added to TreeSet.");
    }

    public static Region create(Selection selection) throws Exception {
        if (intersectsAnotherRegion(selection)) throw new Exception("Selection intersects an already existing Region.");
        if (surroundsAnotherRegion(selection)) throw new Exception("Selection contains an already existing Region.");
        UUID uniqueID = UUID.randomUUID();
        Minelife.SQLITE.query("INSERT INTO plots (uuid, world, minX, minY, minZ, maxX, maxY, maxZ) VALUES (" +
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
            Minelife.SQLITE.query("DELETE FROM plots WHERE uuid = '" + region.uniqueID.toString() + "'");
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

    public static boolean intersectsAnotherRegion(Selection selection) {
        return REGIONS.stream().anyMatch(region -> region.bounds.intersectsWith(selection.getBounds()) && region.worldName.equals(selection.getWorldName()));
    }

    public static boolean surroundsAnotherRegion(Selection selection) {
        return REGIONS.stream().anyMatch(region -> {
           Vec3 rMin = Vec3.createVectorHelper(region.bounds.minX, region.bounds.minY, region.bounds.minZ);
           Vec3 rMax = Vec3.createVectorHelper(region.bounds.maxX, region.bounds.maxY, region.bounds.maxZ);
           return selection.getBounds().isVecInside(rMin) && selection.getBounds().isVecInside(rMax);
        });
    }

    private Region closest;

    public AxisAlignedBB getBounds() {
        return this.bounds;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public Region getParent() {
        Vec3 min = Vec3.createVectorHelper(this.bounds.minX, this.bounds.minY, this.bounds.minZ);
        Vec3 max = Vec3.createVectorHelper(this.bounds.maxX, this.bounds.maxY, this.bounds.maxZ);
        List<Region> enclosing = REGIONS.stream().filter(region -> region.bounds.isVecInside(min) && region.bounds.isVecInside(max)).collect(Collectors.toList());
        if (!enclosing.isEmpty()) {
            if (closest == null) closest = enclosing.get(0);
            enclosing.forEach(region -> {
                Vec3 rMin = Vec3.createVectorHelper(region.bounds.minX, region.bounds.minY, region.bounds.minZ);
                Vec3 rMax = Vec3.createVectorHelper(region.bounds.maxX, region.bounds.maxY, region.bounds.maxZ);
                Vec3 cMin = Vec3.createVectorHelper(closest.bounds.minX, closest.bounds.minY, closest.bounds.minZ);
                Vec3 cMax = Vec3.createVectorHelper(closest.bounds.maxX, closest.bounds.maxY, closest.bounds.maxZ);
                if (rMin.distanceTo(min) + rMax.distanceTo(max) < cMin.distanceTo(min) + cMax.distanceTo(max))
                    closest = region;
            });
            return closest;
        }
        return null;
    }

    public List<Region> getChildren() {
        return REGIONS.stream().filter(region -> region.getParent().equals(this)).collect(Collectors.toList());
    }

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
        if (other != null) {
            if (other.getParent() != null && other.getParent().equals(this)) return 1;
            else if (this.getParent() != null && this.getParent().equals(other)) return -1;
        }
        return 0;
    }

    public static void initRegions() throws SQLException {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM plots");
        while (result.next()) REGIONS.add(new Region(UUID.fromString(result.getString("uuid"))));
    }

}