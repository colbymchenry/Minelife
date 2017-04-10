package com.minelife.region.server;

import com.minelife.Minelife;
import net.minecraft.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class SubRegion extends RegionBase implements Comparable<SubRegion> {

    public static Set<SubRegion> SUB_REGIONS = new TreeSet<>();

    private Region parentRegion;

    private SubRegion(UUID uuid) throws SQLException {
        this.uuid = uuid;

        ResultSet result = Minelife.SQLITE.query("SELECT * FROM subregions WHERE uuid='" + this.uuid.toString() + "'");

        if(!result.next()) throw new SQLException("No subregion exists with that UUID.");

        this.min = new int[]{result.getInt("minX"), result.getInt("minY"), result.getInt("minZ")};
        this.max = new int[]{result.getInt("maxX"), result.getInt("maxY"), result.getInt("maxZ")};

        this.parentRegion = Region.getRegion(UUID.fromString(result.getString("parentregionuuid")));
        this.world = parentRegion.getWorld();
    }

    /**
     * -------------------------- STATIC HANDLERS -------------------------------
     */

    public static void createSubRegion(Region parentRegion, int[] min, int[] max) throws Exception {
        // TODO: Check if it goes outside the parent region
        // TODO: Test Command
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM subregions WHERE world='" + parentRegion.getWorld() + "' AND " +
                "minX <= '" + min[0] + "' AND " +
                "minY <= '" + min[1] + "' AND " +
                "minZ <= '" + min[2] + "' AND " +
                "maxX >= '" + max[0] + "' AND " +
                "maxY >= '" + max[1] + "' AND " +
                "maxZ >= '" + max[2] + "'");

        if (result.next()) throw new Exception("Overlapping another subregion.");

        Minelife.SQLITE.query("INSERT INTO subregions (parentregionuuid, minX, minY, minZ, maxX, maxY, maxZ) " +
                "VALUES ('" + parentRegion.getUUID().toString() + "', " +
                "'" + min[0] + "', '" + min[1] + "', '" + min[2] + "', " +
                "'" + max[0] + "', '" + max[1] + "', '" + max[2] + "')");
    }

    public static void deleteSubRegion(UUID uuid) throws SQLException {
        Minelife.SQLITE.query("DELETE FROM subregions WHERE uuid='" + uuid.toString() + "'");
        SUB_REGIONS.remove(SubRegion.getSubRegion(uuid));
    }

    public static SubRegion getSubRegion(UUID uuid) {
        return SUB_REGIONS.stream().filter(subRegion -> subRegion.uuid.equals(uuid)).findFirst().orElse(null);
    }

    public static SubRegion getSubRegionAt(World world, int x, int y, int z) {
        return SUB_REGIONS.stream().filter(subRegion -> subRegion.doesContain(x, y, z)).findFirst().orElse(null);
    }

    public static void initSubRegions() throws SQLException {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM regions");

        while (result.next())
            SUB_REGIONS.add(new SubRegion(UUID.fromString(result.getString("uuid"))));
    }

    @Override
    public int compareTo(SubRegion o) {
        return o.parentRegion.getUUID().equals(this.parentRegion.getUUID()) ? 0 : 1;
    }
}
