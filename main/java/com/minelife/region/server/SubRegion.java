package com.minelife.region.server;

import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class SubRegion extends RegionBase implements Comparable<SubRegion> {

    public static Set<SubRegion> SUB_REGIONS = new TreeSet<>();

    private Region parentRegion;

    private SubRegion(UUID uuid) throws Exception {
        this.regionUniqueID = uuid;

        ResultSet result = Minelife.SQLITE.query("SELECT * FROM subregions WHERE regionUniqueID='" + this.regionUniqueID.toString() + "'");

        if(!result.next()) throw new CustomMessageException("No subregion exists with that UUID.");

        this.min = new int[]{result.getInt("minX"), result.getInt("minY"), result.getInt("minZ")};
        this.max = new int[]{result.getInt("maxX"), result.getInt("maxY"), result.getInt("maxZ")};

        this.parentRegion = Region.getRegion(UUID.fromString(result.getString("parentregionuuid")));
        this.world = parentRegion.getWorld();
    }

    public Region getParentRegion()
    {
        return parentRegion;
    }

    public static SubRegion createSubRegion(Region parentRegion, int[] min, int[] max) throws Exception {
        AxisAlignedBB subRegionBounds = AxisAlignedBB.getBoundingBox(min[0], min[1], min[2], max[0], max[1], max[2]);
        AxisAlignedBB parentRegionBounds = parentRegion.getAxisAlignedBB();
        UUID subRegionID = UUID.randomUUID();

        if(parentRegionBounds.minX > subRegionBounds.minX ||parentRegionBounds.minY > subRegionBounds.minY || parentRegionBounds.minZ > subRegionBounds.minZ ||
                parentRegionBounds.maxX < subRegionBounds.maxX || parentRegionBounds.maxY < subRegionBounds.maxY || parentRegionBounds.maxZ < subRegionBounds.maxZ) {
            throw new CustomMessageException("The SubRegion falls outside of the Region.");
        }

        SubRegion sub_region = SUB_REGIONS.stream().filter(subRegion -> subRegion.getAxisAlignedBB().intersectsWith(subRegionBounds)).findFirst().orElse(null);
        if(sub_region != null) {
            throw new CustomMessageException("Intersecting another SubRegion.");
        }


        Minelife.SQLITE.query("INSERT INTO subregions (regionUniqueID, parentregionuuid, world, minX, minY, minZ, maxX, maxY, maxZ) " +
                "VALUES ('" + subRegionID.toString() + "', '" + parentRegion.getUniqueID().toString() + "', '" + parentRegion.getWorld() + "', " +
                "'" + min[0] + "', '" + min[1] + "', '" + min[2] + "', " +
                "'" + max[0] + "', '" + max[1] + "', '" + max[2] + "')");

        SubRegion toReturn;

        SUB_REGIONS.add(toReturn = new SubRegion(subRegionID));
        return toReturn;
    }

    public static void deleteSubRegion(UUID uuid) throws SQLException {
        Minelife.SQLITE.query("DELETE FROM subregions WHERE regionUniqueID='" + uuid.toString() + "'");
        SUB_REGIONS.remove(SubRegion.getSubRegion(uuid));
    }

    public static SubRegion getSubRegion(UUID uuid) {
        return SUB_REGIONS.stream().filter(subRegion -> subRegion.regionUniqueID.equals(uuid)).findFirst().orElse(null);
    }

    public static SubRegion getSubRegionAt(World world, int x, int y, int z) {
        return SUB_REGIONS.stream().filter(subRegion -> subRegion.doesContain(x, y, z)).findFirst().orElse(null);
    }

    public static void initSubRegions() throws Exception {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM subregions");

        while (result.next())
            SUB_REGIONS.add(new SubRegion(UUID.fromString(result.getString("regionUniqueID"))));
    }

    public static SubRegion getIntersectingRegion(AxisAlignedBB axisAlignedBB)
    {
        return SUB_REGIONS.stream().filter(region -> region.getAxisAlignedBB().intersectsWith(axisAlignedBB)).findFirst().orElse(null);
    }

    @Override
    public int compareTo(SubRegion o) {
        return o.parentRegion.getUniqueID().equals(this.parentRegion.getUniqueID()) ? 0 : 1;
    }
}
