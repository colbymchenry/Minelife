package com.minelife.region.server;

import com.minelife.Minelife;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class Region extends RegionBase implements Comparable<Region> {

    public static final Set<Region> REGIONS = new TreeSet<>();

    private Region(UUID uuid) throws SQLException {
        this.uuid = uuid;

        ResultSet result = Minelife.SQLITE.query("SELECT * FROM regions WHERE uuid='" + this.uuid.toString() + "'");

        if(!result.next()) throw new SQLException("No region exists with that UUID.");

        this.min = new int[]{result.getInt("minX"), result.getInt("minY"), result.getInt("minZ")};
        this.max = new int[]{result.getInt("maxX"), result.getInt("maxY"), result.getInt("maxZ")};

        this.world = result.getString("world");
    }

    public static void createRegion(String world, int[] min, int[] max) throws Exception {
        AxisAlignedBB axisAlignedBB = AxisAlignedBB.getBoundingBox(min[0], min[1], min[2], max[0], max[1], max[2]);

        Region foundRegion = REGIONS.stream().filter(region -> region.getAxisAlignedBB().intersectsWith(axisAlignedBB)).findFirst().orElse(null);

        if (foundRegion != null) throw new Exception("Overlapping another region.");

        UUID regionID = UUID.randomUUID();

        Minelife.SQLITE.query("INSERT INTO regions (uuid, world, minX, minY, minZ, maxX, maxY, maxZ) " +
                "VALUES ('" + regionID.toString() + "', '" + world + "', " +
                "'" + min[0] + "', '" + min[1] + "', '" + min[2] + "', " +
                "'" + max[0] + "', '" + max[1] + "', '" + max[2] + "')");

        REGIONS.add(new Region(regionID));
    }

    public static void deleteRegion(UUID uuid) throws SQLException {
        Minelife.SQLITE.query("DELETE FROM regions WHERE uuid='" + uuid.toString() + "'");
        REGIONS.remove(Region.getRegion(uuid));
    }

    public static Region getRegion(UUID uuid) {
        return REGIONS.stream().filter(region -> region.uuid.equals(uuid)).findFirst().orElse(null);
    }

    public static Region getRegionAt(World world, int x, int y, int z) {
        return REGIONS.stream().filter(region -> region.doesContain(x, y, z)).findFirst().orElse(null);
    }

    public static void initRegions() throws SQLException {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM regions");

        while (result.next())
            REGIONS.add(new Region(UUID.fromString(result.getString("uuid"))));
    }

    @Override
    public int compareTo(Region o) {
        return o.uuid.equals(this.uuid) ? 0 : 1;
    }
}
