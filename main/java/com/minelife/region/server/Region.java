package com.minelife.region.server;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class Region implements Comparable<Region> {

    private static final Set<Region> REGIONS = new TreeSet<>();

    private UUID regionUniqueID;
    private AxisAlignedBB bounds;
    private String world;

    private Region(UUID regionUniqueID)
    {

    }

    public UUID getUniqueID()
    {
        return this.regionUniqueID;
    }

    public String getWorldName()
    {
        return world;
    }

    @SideOnly(Side.SERVER)
    public World getEntityWorld()
    {
        for (WorldServer worldServer : MinecraftServer.getServer().worldServers) {
            if (worldServer.getWorldInfo().getWorldName().equalsIgnoreCase(getWorldName()))
                return worldServer;
        }
        return null;
    }

    public AxisAlignedBB getBounds()
    {
        return bounds;
    }

    public Region getParentRegion()
    {
        return REGIONS.stream().filter(region ->
                region.getBounds().isVecInside(Vec3.createVectorHelper(getBounds().minX, getBounds().minY, getBounds().minZ)) &&
                        region.getBounds().isVecInside(Vec3.createVectorHelper(getBounds().maxX, getBounds().maxY, getBounds().maxX))).findFirst().orElse(null);
    }

    public boolean isSubRegion()
    {
        return getParentRegion() != null;
    }

    @Override
    public int compareTo(Region o)
    {
        return o.getUniqueID().equals(getUniqueID()) ? 0 : 1;
    }

    public static Region create(String world, AxisAlignedBB bounds) throws Exception
    {
        // Check for a parent region
        Region parentRegion = Region.getContainingRegion(world, bounds);

        if (parentRegion != null) {
            // If we have a parent region check if it is a sub region
            if (parentRegion.isSubRegion()) throw new Exception("SubRegion cannot contain a region.");
            //check if intersects with another SubRegion
            if (Region.getIntersectingRegion(world, bounds, parentRegion) != null)
                throw new Exception("Intersecting with a SubRegion.");
        } else {
            // If we have an intersecting region do not create a region
            if (Region.getIntersectingRegion(world, bounds) != null)
                throw new Exception("Intersecting with another region.");
        }

        UUID regionUniqueID = UUID.randomUUID();

        // create region
        Minelife.SQLITE.query("INSERT INTO regions (uuid, world, minX, minY, minZ, maxX, maxY, maxZ) VALUES (" +
                "'" + regionUniqueID.toString() + "'," +
                "'" + world + "'," +
                "'" + ((int) bounds.minX) + "', '" + ((int) bounds.minY) + "', '" + ((int) bounds.minZ) + "'," +
                "'" + ((int) bounds.maxX) + "', '" + ((int) bounds.maxY) + "', '" + ((int) bounds.maxZ) + "')");
    }

    public static void delete(UUID regionUniqueID)
    {

    }

    public static Region getIntersectingRegion(String world, AxisAlignedBB bounds)
    {
        return REGIONS.stream().filter(region -> region.getBounds().intersectsWith(bounds) && region.getWorldName().equalsIgnoreCase(world)).findFirst().orElse(null);
    }

    public static Region getIntersectingRegion(String world, AxisAlignedBB bounds, Region parentRegion)
    {
        return REGIONS.stream().filter(region ->
                region.isSubRegion() &&
                        region.getParentRegion().getUniqueID().equals(parentRegion.getUniqueID()) &&
                        region.getBounds().intersectsWith(bounds) &&
                        region.getWorldName().equalsIgnoreCase(world)).findFirst().orElse(null);
    }

    public static Region getContainingRegion(String world, AxisAlignedBB bounds)
    {
        return REGIONS.stream().filter(region ->
                region.getBounds().isVecInside(Vec3.createVectorHelper(bounds.minX, bounds.minY, bounds.minZ)) &&
                        region.getBounds().isVecInside(Vec3.createVectorHelper(bounds.maxX, bounds.maxY, bounds.maxX)) &&
                        world.equalsIgnoreCase(region.getWorldName())).findFirst().orElse(null);
    }

    public static Region getRegionAt(String world, Vec3 vec3)
    {
        List<Region> regions = Lists.newArrayList();
        for (Region r : REGIONS) {
            if (r.getBounds().isVecInside(vec3) && r.getWorldName().equalsIgnoreCase(world)) {
                regions.add(r);
            }
        }

        Region closestRegion = regions.get(0);

        for(Region r : regions) {
            int minXOffset = (int) (r.getBounds().minX - vec3.xCoord);
            int minYOffset = (int) (r.getBounds().minY - vec3.yCoord);
            int minZOffset = (int) (r.getBounds().minZ - vec3.zCoord);
            int maxXOffset = (int) (r.getBounds().maxX - vec3.xCoord);
            int maxYOffset = (int) (r.getBounds().maxY - vec3.yCoord);
            int maxZOffset = (int) (r.getBounds().maxZ - vec3.zCoord);

            int closestRegionMinXOffset = (int) (closestRegion.getBounds().minX - vec3.xCoord);
            int closestRegionMinYOffset = (int) (closestRegion.getBounds().minY - vec3.yCoord);
            int closestRegionMinZOffset = (int) (closestRegion.getBounds().minZ - vec3.zCoord);
            int closestRegionMaxXOffset = (int) (closestRegion.getBounds().maxX - vec3.xCoord);
            int closestRegionMaxYOffset = (int) (closestRegion.getBounds().maxY - vec3.yCoord);
            int closestRegionMaxZOffset = (int) (closestRegion.getBounds().maxZ - vec3.zCoord);

            if(minXOffset < closestRegionMinXOffset && maxXOffset < closestRegionMaxXOffset &&
                    minYOffset < closestRegionMinYOffset && maxYOffset < closestRegionMaxYOffset &&
                    minZOffset < closestRegionMinZOffset && maxZOffset < closestRegionMaxZOffset) {
                closestRegion = r;
            }
        }

        return closestRegion;
    }


    public static Region getRegionFromUUID(UUID regionUniqueID)
    {

    }

}
