package com.minelife.region.server;

import com.google.common.collect.Lists;
import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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

    @SideOnly(Side.CLIENT)
    public World getEntityWorldClient()
    {
        return Minecraft.getMinecraft().theWorld;
    }

    public AxisAlignedBB getBounds()
    {
        return bounds;
    }

    public boolean contains(String world, double x, double y, double z)
    {
        return world.equals(this.world) && (bounds.isVecInside(Vec3.createVectorHelper(x, y, z)) || bounds.isVecInside(Vec3.createVectorHelper(x - 1, y - 1, z - 1)));
    }

    public boolean contains(Region region)
    {
        return contains(region.world, region.getBounds().minX, region.getBounds().minY, region.getBounds().minZ) && contains(region.world, region.getBounds().maxX, region.getBounds().maxY, region.getBounds().maxZ);
    }

    public boolean contains(EntityPlayer player) {
        return contains(player.getEntityWorld().getWorldInfo().getWorldName(), player.posX, player.posY, player.posZ);
    }

    public Region getParentRegion()
    {
        return Region.getContainingRegion(getWorldName(), getBounds());
    }

    public List<Region> getContainingRegions()
    {
        List<Region> containingRegions = Lists.newArrayList();
        for (Region region : REGIONS) {
            if (!region.getUniqueID().equals(getUniqueID()) && isVecInside(Vec3.createVectorHelper(region.bounds.minX, region.bounds.minY, region.bounds.minZ)) &&
                    isVecInside(Vec3.createVectorHelper(region.bounds.maxX, region.bounds.maxY, region.bounds.maxZ)))
                containingRegions.add(region);
        }

        List<Region> regionsInOrder = Lists.newArrayList();
        double difference = 100;
        Region r = null;
        for (int i = 0; i < containingRegions.size(); i++) {
            for (Region containingRegion : containingRegions) {
                if (containingRegion.bounds.minX - bounds.minX < difference && !regionsInOrder.contains(containingRegion)) {
                    difference = containingRegion.bounds.minX - bounds.minX;
                    r = containingRegion;
                }
            }
            regionsInOrder.add(r);
        }

        return regionsInOrder;
    }

    public Region getMasterRegion()
    {
        Region region = this;
        while (region.getParentRegion() != null) region = region.getParentRegion();
        return region;
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
                throw new CustomMessageException("Intersecting with another region2.");
        } else {
            // If we have an intersecting region do not create a region
            if (Region.getIntersectingRegion(world, bounds) != null)
                throw new CustomMessageException("Intersecting with another region1.");
        }

        UUID regionUniqueID = UUID.randomUUID();

        CuboidRegion cuboidRegion = new CuboidRegion(new Vector(bounds.minX, bounds.minY, bounds.minZ), new Vector(bounds.maxX, bounds.maxY, bounds.maxZ));

        // create region
        Minelife.SQLITE.query("INSERT INTO regions (uuid, world, minX, minY, minZ, maxX, maxY, maxZ) VALUES (" +
                "'" + regionUniqueID.toString() + "'," +
                "'" + world + "'," +
                "'" + cuboidRegion.getMinimumPoint().getBlockX() + "', '" + cuboidRegion.getMinimumPoint().getBlockY() + "', '" + cuboidRegion.getMinimumPoint().getBlockZ() + "'," +
                "'" + cuboidRegion.getMaximumPoint().getBlockX() + "', '" + cuboidRegion.getMaximumPoint().getBlockY() + "', '" + cuboidRegion.getMaximumPoint().getBlockZ() + "')");

        Region region = new Region(regionUniqueID);
        REGIONS.add(region);
        return region;
    }

    public static void delete(UUID regionUniqueID) throws SQLException
    {
        Region region = getRegionFromUUID(regionUniqueID);
        Minelife.SQLITE.query("DELETE FROM regions WHERE uuid='" + regionUniqueID.toString() + "'");
        REGIONS.remove(region);
    }

    public static Region getIntersectingRegion(String world, AxisAlignedBB bounds)
    {
        Region containingRegion = getContainingRegion(world, bounds);

        if (containingRegion != null) {
            Set<Region> parentRegions = new TreeSet<>();

            if (!containingRegion.isSubRegion()) {
                for (Region r : REGIONS) {
                    if (!r.equals(containingRegion)) {
                        CuboidRegion cuboidRegion = new CuboidRegion(new Vector(r.getBounds().minX, r.getBounds().minY, r.getBounds().minZ),
                                new Vector(r.getBounds().maxX, r.getBounds().maxY, r.getBounds().maxZ));

                        boolean intersects = bounds.maxX >= cuboidRegion.getMinimumPoint().getBlockX() && bounds.minX <= cuboidRegion.getMaximumPoint().getBlockX() ? (bounds.maxY >= cuboidRegion.getMinimumPoint().getBlockY() && bounds.minY <= cuboidRegion.getMaximumPoint().getBlockY() ? bounds.maxZ >= cuboidRegion.getMinimumPoint().getBlockZ() && bounds.minZ <= cuboidRegion.getMaximumPoint().getBlockZ() : false) : false;

                        if (intersects && r.getWorldName().equalsIgnoreCase(world)) return r;
                    }
                }

                return null;
            }


            parentRegions.add(containingRegion);
            Region parentRegion = containingRegion.getParentRegion();

            parentRegion.getEntityWorld().setBlock((int) parentRegion.getBounds().minX, (int) parentRegion.getBounds().maxY, (int) parentRegion.getBounds().minZ, Blocks.diamond_block);
            containingRegion.getEntityWorld().setBlock((int) containingRegion.getBounds().minX, (int) containingRegion.getBounds().maxY, (int) containingRegion.getBounds().minZ, Blocks.gold_block);

            while (parentRegion != null) {
                parentRegions.add(parentRegion);
                parentRegion = parentRegion.getParentRegion();
            }

            for (Region r : REGIONS) {
                if (!parentRegions.contains(r)) {
                    CuboidRegion cuboidRegion = new CuboidRegion(new Vector(r.getBounds().minX, r.getBounds().minY, r.getBounds().minZ),
                            new Vector(r.getBounds().maxX, r.getBounds().maxY, r.getBounds().maxZ));

                    boolean intersects = bounds.maxX >= cuboidRegion.getMinimumPoint().getBlockX() && bounds.minX <= cuboidRegion.getMaximumPoint().getBlockX() ? (bounds.maxY >= cuboidRegion.getMinimumPoint().getBlockY() && bounds.minY <= cuboidRegion.getMaximumPoint().getBlockY() ? bounds.maxZ >= cuboidRegion.getMinimumPoint().getBlockZ() && bounds.minZ <= cuboidRegion.getMaximumPoint().getBlockZ() : false) : false;

                    if (intersects && r.getWorldName().equalsIgnoreCase(world)) return r;
                }
            }

            return null;
        } else {
            for (Region r : REGIONS) {
                CuboidRegion cuboidRegion = new CuboidRegion(new Vector(r.getBounds().minX, r.getBounds().minY, r.getBounds().minZ),
                        new Vector(r.getBounds().maxX, r.getBounds().maxY, r.getBounds().maxZ));

                boolean intersects = bounds.maxX >= cuboidRegion.getMinimumPoint().getBlockX() && bounds.minX <= cuboidRegion.getMaximumPoint().getBlockX() ? (bounds.maxY >= cuboidRegion.getMinimumPoint().getBlockY() && bounds.minY <= cuboidRegion.getMaximumPoint().getBlockY() ? bounds.maxZ >= cuboidRegion.getMinimumPoint().getBlockZ() && bounds.minZ <= cuboidRegion.getMaximumPoint().getBlockZ() : false) : false;

                if (intersects && r.getWorldName().equalsIgnoreCase(world)) return r;
            }
            return null;
        }
    }

    public static Region getContainingRegion(String world, AxisAlignedBB bounds)
    {
        Set<Region> regions = new TreeSet<>();
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
        Set<Region> regions = new TreeSet<>();
        for (Region r : REGIONS) {
            CuboidRegion cuboidRegion = new CuboidRegion(new Vector(r.getBounds().minX, r.getBounds().minY, r.getBounds().minZ),
                    new Vector(r.getBounds().maxX, r.getBounds().maxY, r.getBounds().maxZ));
            if (cuboidRegion.contains(new Vector(vec3.xCoord, vec3.yCoord, vec3.zCoord)) && r.getWorldName().equalsIgnoreCase(world)) {
                regions.add(r);
            }
        }

        if (regions.isEmpty()) return null;

        return getClosest(regions, vec3);
    }


    public static Region getRegionAt(World world, Vec3 vec3)
    {
        return getRegionAt(world.getWorldInfo().getWorldName(), vec3);
    }

    private static Region getClosest(Set<Region> regions, Vec3 vec)
    {
        Region closestRegion = (Region) regions.toArray()[0];
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
