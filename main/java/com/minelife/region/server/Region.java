package com.minelife.region.server;

import com.google.common.collect.Lists;
import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class Region implements Comparable<Region> {

    private static final Set<Region> REGIONS = new TreeSet<>();

    private UUID regionUniqueID;
    private AxisAlignedBB bounds;
    private String world;

    private Region(UUID regionUniqueID) throws SQLException
    {
        this.regionUniqueID = regionUniqueID;
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM regions WHERE uuid='" + regionUniqueID.toString() + "'");
        bounds = AxisAlignedBB.getBoundingBox(result.getInt("minX"), result.getInt("minY"), result.getInt("minZ"),
                result.getInt("maxX"), result.getInt("maxY"), result.getInt("maxZ"));
        world = result.getString("world");
    }

    private Region()
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
        return Region.getContainingRegion(getWorldName(), getBounds());
    }

    public boolean isSubRegion()
    {
        return getParentRegion() != null;
    }

    public boolean isVecInside(Vec3 vec3)
    {
        return bounds.isVecInside(vec3);
    }

    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, getUniqueID().toString());
        ByteBufUtils.writeUTF8String(buf, getWorldName());
        buf.writeDouble(bounds.minX);
        buf.writeDouble(bounds.minY);
        buf.writeDouble(bounds.minZ);
        buf.writeDouble(bounds.maxX);
        buf.writeDouble(bounds.maxY);
        buf.writeDouble(bounds.maxZ);
    }

    public static Region fromBytes(ByteBuf buf)
    {
        Region region = new Region();
        region.regionUniqueID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        region.world = ByteBufUtils.readUTF8String(buf);
        region.bounds = AxisAlignedBB.getBoundingBox(buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readDouble(), buf.readDouble(), buf.readDouble());
        return region;
    }

    @Override
    public int compareTo(Region o)
    {
        return o.getUniqueID().equals(getUniqueID()) ? 0 : 1;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Region && (((Region) obj).getUniqueID().equals(getUniqueID()));
    }

    public static Region create(String world, AxisAlignedBB bounds) throws Exception
    {
        // Check for a parent region
        Region parentRegion = Region.getContainingRegion(world, bounds);

        if (parentRegion != null) {
            //check if intersects with another SubRegion
            if (Region.getIntersectingRegion(world, bounds) != null)
                throw new CustomMessageException("Intersecting with another region.");
        } else {
            // If we have an intersecting region do not create a region
            if (Region.getIntersectingRegion(world, bounds) != null)
                throw new CustomMessageException("Intersecting with another region.");
        }

        UUID regionUniqueID = UUID.randomUUID();

        // create region
        Minelife.SQLITE.query("INSERT INTO regions (uuid, world, minX, minY, minZ, maxX, maxY, maxZ) VALUES (" +
                "'" + regionUniqueID.toString() + "'," +
                "'" + world + "'," +
                "'" + ((int) bounds.minX) + "', '" + ((int) bounds.minY) + "', '" + ((int) bounds.minZ) + "'," +
                "'" + ((int) bounds.maxX) + "', '" + ((int) bounds.maxY) + "', '" + ((int) bounds.maxZ) + "')");

        Region region = new Region(regionUniqueID);
        REGIONS.add(region);
        return region;
    }

    public static void delete(UUID regionUniqueID) throws SQLException
    {
        Region region = getRegionFromUUID(regionUniqueID);
        region.getEntityWorld().setBlock((int) region.getBounds().minX, (int) region.getBounds().minY, (int) region.getBounds().minZ, Blocks.gold_block);
        Minelife.SQLITE.query("DELETE FROM regions WHERE uuid='" + regionUniqueID.toString() + "'");
        REGIONS.remove(region);
    }

    public static Region getIntersectingRegion(String world, AxisAlignedBB bounds)
    {
        Region containingRegion = getContainingRegion(world, bounds);

        if (containingRegion != null) {
            List<Region> parentRegions = Lists.newArrayList();

            if(!containingRegion.isSubRegion())
                return REGIONS.stream().filter(region -> !region.equals(containingRegion) && region.getBounds().intersectsWith(bounds) && region.getWorldName().equalsIgnoreCase(world)).findFirst().orElse(null);

            Region parentRegion = containingRegion.getParentRegion();

            parentRegions.add(containingRegion);

            while(parentRegion != null) {
                parentRegions.add(parentRegion);
                parentRegion = parentRegion.getParentRegion();
            }

            return REGIONS.stream().filter(region -> !parentRegions.contains(region) && region.getBounds().intersectsWith(bounds) && region.getWorldName().equalsIgnoreCase(world)).findFirst().orElse(null);
        } else {
            return REGIONS.stream().filter(region -> region.getBounds().intersectsWith(bounds) && region.getWorldName().equalsIgnoreCase(world)).findFirst().orElse(null);
        }
    }

    public static Region getContainingRegion(String world, AxisAlignedBB bounds)
    {
        List<Region> regions = Lists.newArrayList();
        for (Region r : REGIONS) {
            if (r.getBounds().isVecInside(Vec3.createVectorHelper(bounds.minX, bounds.minY, bounds.minZ)) &&
                    r.getBounds().isVecInside(Vec3.createVectorHelper(bounds.maxX, bounds.maxY, bounds.maxZ)) &&
                    world.equalsIgnoreCase(r.getWorldName())) {
                regions.add(r);
            }
        }

        if (regions.isEmpty()) return null;

        return getClosest(regions, Vec3.createVectorHelper(bounds.minX, bounds.minY, bounds.minZ));
    }

    public static Region getRegionAt(String world, Vec3 vec3)
    {
        List<Region> regions = Lists.newArrayList();
        for (Region r : REGIONS) {
            if (r.getBounds().isVecInside(vec3) && r.getWorldName().equalsIgnoreCase(world)) {
                regions.add(r);
            }
        }

        if (regions.isEmpty()) return null;

        return getClosest(regions, vec3);
    }

    private static Region getClosest(List<Region> regions, Vec3 vec) {
        Region closestRegion = regions.get(0);
        for (Region r : regions) {
            double distance = Vec3.createVectorHelper(r.getBounds().minX, r.getBounds().minY, r.getBounds().minZ).distanceTo(vec);
            double closestRegionDistance = Vec3.createVectorHelper(closestRegion.getBounds().minX, closestRegion.getBounds().minY, closestRegion.getBounds().minZ).distanceTo(vec);
            if (distance < closestRegionDistance) closestRegion = r;
        }

        return closestRegion;
    }

    public static Region getRegionFromUUID(UUID regionUniqueID)
    {
        return REGIONS.stream().filter(region -> region.getUniqueID().equals(regionUniqueID)).findFirst().orElse(null);
    }

    public static void initRegions() throws SQLException
    {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM regions");
        while (result.next()) REGIONS.add(new Region(UUID.fromString(result.getString("uuid"))));
    }

}
