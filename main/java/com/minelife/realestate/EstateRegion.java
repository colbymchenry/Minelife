package com.minelife.realestate;

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
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class EstateRegion implements Comparable<EstateRegion> {

    private static final Set<EstateRegion> ESTATE_REGIONS = new TreeSet<>();

    private UUID regionUniqueID;
    private AxisAlignedBB bounds;
    private String world;

    private EstateRegion(UUID regionUniqueID) throws SQLException {
        this.regionUniqueID = regionUniqueID;
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM ESTATE_REGIONS WHERE uuid = '" + regionUniqueID.toString() + "'");
        this.bounds = AxisAlignedBB.getBoundingBox(result.getInt("minX"), result.getInt("minY"), result.getInt("minZ"),
                result.getInt("maxX"), result.getInt("maxY"), result.getInt("maxZ"));
        this.world = result.getString("world");
    }

    private EstateRegion() { }

    public UUID getUniqueID() {
        return this.regionUniqueID;
    }

    private String getWorldName() {
        return this.world;
    }

    @SideOnly(Side.SERVER)
    public World getEntityWorld() {
        for (WorldServer worldServer : MinecraftServer.getServer().worldServers) if (worldServer.getWorldInfo().getWorldName().equalsIgnoreCase(this.world)) return worldServer;
        return null;
    }

    @SideOnly(Side.CLIENT)
    public World getEntityWorldClient() {
        return Minecraft.getMinecraft().theWorld;
    }

    public AxisAlignedBB getBounds() {
        return this.bounds;
    }

    public boolean contains(World world, double x, double y, double z) {
        return Objects.equals(world.getWorldInfo().getWorldName(), this.world) && (this.bounds.isVecInside(Vec3.createVectorHelper(x, y, z)) || this.bounds.isVecInside(Vec3.createVectorHelper(x - 1, y - 1, z - 1)));
    }

    private EstateRegion getParentRegion() {
        return EstateRegion.getContainingRegion(this.world, this.bounds);
    }

    public List<EstateRegion> getContainingRegions() {
        List<EstateRegion> containingRegions = Lists.newArrayList();
        for (EstateRegion region : ESTATE_REGIONS) {
            if(!region.getUniqueID().equals(getUniqueID()) && isVecInside(Vec3.createVectorHelper(region.bounds.minX, region.bounds.minY, region.bounds.minZ)) &&
                    isVecInside(Vec3.createVectorHelper(region.bounds.maxX, region.bounds.maxY, region.bounds.maxZ)))
                containingRegions.add(region);
        }

        List<EstateRegion> regionsInOrder = Lists.newArrayList();
        double difference = 100;
        EstateRegion r = null;
        for(int i = 0; i < containingRegions.size(); i++) {
            for (EstateRegion containingRegion : containingRegions) {
                if (containingRegion.bounds.minX - bounds.minX < difference && !regionsInOrder.contains(containingRegion)) {
                    difference = containingRegion.bounds.minX - bounds.minX;
                    r = containingRegion;
                }
            }
            regionsInOrder.add(r);
        }

        return regionsInOrder;
    }

    public EstateRegion getMasterRegion() {
        EstateRegion region = this;
        while(region.getParentRegion() != null) region = region.getParentRegion();
        return region;
    }

    public boolean isSubRegion() {
        return getParentRegion() != null;
    }

    public boolean isVecInside(Vec3 vec3) {
        return bounds.isVecInside(vec3);
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, getUniqueID().toString());
        ByteBufUtils.writeUTF8String(buf, getWorldName());
        buf.writeDouble(bounds.minX);
        buf.writeDouble(bounds.minY);
        buf.writeDouble(bounds.minZ);
        buf.writeDouble(bounds.maxX);
        buf.writeDouble(bounds.maxY);
        buf.writeDouble(bounds.maxZ);
    }

    public static EstateRegion fromBytes(ByteBuf buf) {
        EstateRegion region = new EstateRegion();
        region.regionUniqueID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        region.world = ByteBufUtils.readUTF8String(buf);
        region.bounds = AxisAlignedBB.getBoundingBox(buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readDouble(), buf.readDouble(), buf.readDouble());
        return region;
    }

    /**
     *
     * @param estateRegion the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *          is contained in, disjoint from, or contains the specified object.
     *
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this object.
     *
     */
    @Override
    public int compareTo(EstateRegion estateRegion) {
        if (estateRegion == null) return 0;
        if (EstateRegion.getContainingRegion(this.world, this.bounds) != null && EstateRegion.getContainingRegion(this.world, this.bounds).equals(estateRegion)) return -1;
        if (EstateRegion.getContainingRegion(estateRegion.world, estateRegion.bounds) != null && EstateRegion.getContainingRegion(estateRegion.world, estateRegion.bounds).equals(this)) return 1;
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof EstateRegion && ((EstateRegion) obj).getUniqueID().equals(getUniqueID());
    }

    public static EstateRegion create(String world, AxisAlignedBB bounds) throws SQLException, CustomMessageException {
        // Check for a parent region
        EstateRegion parentRegion = EstateRegion.getContainingRegion(world, bounds);

        if (parentRegion != null) {
            //check if intersects with another SubRegion
            if (EstateRegion.getIntersectingRegion(world, bounds) != null)
                throw new CustomMessageException("Intersecting with another region2.");
        } else {
            // If we have an intersecting region do not create a region
            if (EstateRegion.getIntersectingRegion(world, bounds) != null) throw new CustomMessageException("Intersecting with another region1.");
        }

        // Throw Exception is similar region exists
        if (ESTATE_REGIONS.stream().anyMatch(region -> bounds.minX == region.bounds.minX &&
                                                bounds.minY == region.bounds.minY &&
                                                bounds.minZ == region.bounds.minZ &&
                                                bounds.maxX == region.bounds.maxX &&
                                                bounds.maxY == region.bounds.maxY &&
                                                bounds.maxZ == region.bounds.maxZ)) throw new CustomMessageException("Has same bounds as another region.");

        UUID regionUniqueID = UUID.randomUUID();

        CuboidRegion cuboidRegion = new CuboidRegion(new Vector(bounds.minX, bounds.minY, bounds.minZ), new Vector(bounds.maxX, bounds.maxY, bounds.maxZ));

        // create region
        Minelife.SQLITE.query("INSERT INTO ESTATE_REGIONS (uuid, world, minX, minY, minZ, maxX, maxY, maxZ) VALUES (" +
                "'" + regionUniqueID.toString() + "'," +
                "'" + world + "'," +
                "'" + cuboidRegion.getMinimumPoint().getBlockX() + "', '" + cuboidRegion.getMinimumPoint().getBlockY() + "', '" + cuboidRegion.getMinimumPoint().getBlockZ() + "'," +
                "'" +  cuboidRegion.getMaximumPoint().getBlockX() + "', '" +  cuboidRegion.getMaximumPoint().getBlockY()  + "', '" +  cuboidRegion.getMaximumPoint().getBlockZ()  + "')");

        EstateRegion region = new EstateRegion(regionUniqueID);
        ESTATE_REGIONS.add(region);
        return region;
    }

    public static void delete(UUID regionUniqueID) throws SQLException {
        EstateRegion region = getRegionFromUUID(regionUniqueID);
        Minelife.SQLITE.query("DELETE FROM ESTATE_REGIONS WHERE uuid = '" + regionUniqueID.toString() + "'");
        if (region != null) ESTATE_REGIONS.remove(region);
    }

    public static EstateRegion getIntersectingRegion(String world, AxisAlignedBB bounds) {
        EstateRegion containingRegion = getContainingRegion(world, bounds);

        if (containingRegion != null) {
            Set<EstateRegion> parentRegions = new TreeSet<>();

            if (!containingRegion.isSubRegion()) {
                for (EstateRegion region : ESTATE_REGIONS) {
                    if (!region.equals(containingRegion)) {
                        CuboidRegion cuboidRegion = new CuboidRegion(new Vector(region.getBounds().minX, region.getBounds().minY, region.getBounds().minZ),
                                new Vector(region.getBounds().maxX, region.getBounds().maxY, region.getBounds().maxZ));

                        boolean intersects = (bounds.maxX >= cuboidRegion.getMinimumPoint().getBlockX() && bounds.minX <= cuboidRegion.getMaximumPoint().getBlockX()) && ((bounds.maxY >= cuboidRegion.getMinimumPoint().getBlockY() && bounds.minY <= cuboidRegion.getMaximumPoint().getBlockY()) && (bounds.maxZ >= cuboidRegion.getMinimumPoint().getBlockZ() && bounds.minZ <= cuboidRegion.getMaximumPoint().getBlockZ()));

                        if (intersects && region.getWorldName().equalsIgnoreCase(world)) return region;
                    }
                }

                return null;
            }


            parentRegions.add(containingRegion);
            EstateRegion parentRegion = containingRegion.getParentRegion();

            parentRegion.getEntityWorld().setBlock((int) parentRegion.getBounds().minX, (int) parentRegion.getBounds().maxY, (int) parentRegion.getBounds().minZ, Blocks.diamond_block);
            containingRegion.getEntityWorld().setBlock((int) containingRegion.getBounds().minX, (int) containingRegion.getBounds().maxY, (int) containingRegion.getBounds().minZ, Blocks.gold_block);

            while (parentRegion != null) {
                parentRegions.add(parentRegion);
                parentRegion = parentRegion.getParentRegion();
            }

            for (EstateRegion region : ESTATE_REGIONS) {
                if (!parentRegions.contains(region)) {
                    CuboidRegion cuboidRegion = new CuboidRegion(new Vector(region.getBounds().minX, region.getBounds().minY, region.getBounds().minZ),
                            new Vector(region.getBounds().maxX, region.getBounds().maxY, region.getBounds().maxZ));

                    boolean intersects = (bounds.maxX >= cuboidRegion.getMinimumPoint().getBlockX() && bounds.minX <= cuboidRegion.getMaximumPoint().getBlockX()) && ((bounds.maxY >= cuboidRegion.getMinimumPoint().getBlockY() && bounds.minY <= cuboidRegion.getMaximumPoint().getBlockY()) && (bounds.maxZ >= cuboidRegion.getMinimumPoint().getBlockZ() && bounds.minZ <= cuboidRegion.getMaximumPoint().getBlockZ()));

                    if (intersects && region.getWorldName().equalsIgnoreCase(world)) return region;
                }
            }

            return null;
        } else {
            for (EstateRegion region : ESTATE_REGIONS) {
                CuboidRegion cuboidRegion = new CuboidRegion(new Vector(region.getBounds().minX, region.getBounds().minY, region.getBounds().minZ),
                        new Vector(region.getBounds().maxX, region.getBounds().maxY, region.getBounds().maxZ));

                boolean intersects = (bounds.maxX >= cuboidRegion.getMinimumPoint().getBlockX() && bounds.minX <= cuboidRegion.getMaximumPoint().getBlockX()) && ((bounds.maxY >= cuboidRegion.getMinimumPoint().getBlockY() && bounds.minY <= cuboidRegion.getMaximumPoint().getBlockY()) && (bounds.maxZ >= cuboidRegion.getMinimumPoint().getBlockZ() && bounds.minZ <= cuboidRegion.getMaximumPoint().getBlockZ()));

                if (intersects && region.getWorldName().equalsIgnoreCase(world)) return region;
            }
            return null;
        }
    }

    public static EstateRegion getContainingRegion(String world, AxisAlignedBB bounds) {
        Set<EstateRegion> regions = new TreeSet<>();
        for (EstateRegion region : ESTATE_REGIONS) {
            if (region.getBounds().isVecInside(Vec3.createVectorHelper(bounds.minX, bounds.minY, bounds.minZ)) &&
                    region.getBounds().isVecInside(Vec3.createVectorHelper(bounds.maxX, bounds.maxY, bounds.maxZ)) &&
                    world.equalsIgnoreCase(region.getWorldName())) {
                regions.add(region);
            }
        }

        if (regions.isEmpty()) return null;

        return getClosest(regions, Vec3.createVectorHelper(bounds.minX, bounds.minY, bounds.minZ));
    }

    public static EstateRegion getRegionAt(String world, Vec3 vec3) {
        Set<EstateRegion> regions = new TreeSet<>();
        for (EstateRegion r : ESTATE_REGIONS) {
            CuboidRegion cuboidRegion = new CuboidRegion(new Vector(r.getBounds().minX, r.getBounds().minY, r.getBounds().minZ),
                    new Vector(r.getBounds().maxX, r.getBounds().maxY, r.getBounds().maxZ));
            if (cuboidRegion.contains(new Vector(vec3.xCoord, vec3.yCoord, vec3.zCoord)) && r.getWorldName().equalsIgnoreCase(world)) {
                regions.add(r);
            }
        }

        if (regions.isEmpty()) return null;

        return getClosest(regions, vec3);
    }

    public static EstateRegion getRegionAt(World world, Vec3 vec3) {
        return getRegionAt(world.getWorldInfo().getWorldName(), vec3);
    }

    private static EstateRegion getClosest(Set<EstateRegion> regions, Vec3 vec) {
        EstateRegion closestRegion = (EstateRegion) regions.toArray()[0];
        for (EstateRegion r : regions) {
            double distance = Vec3.createVectorHelper(r.getBounds().minX, r.getBounds().minY, r.getBounds().minZ).distanceTo(vec);
            double closestRegionDistance = Vec3.createVectorHelper(closestRegion.getBounds().minX, closestRegion.getBounds().minY, closestRegion.getBounds().minZ).distanceTo(vec);
            if (distance < closestRegionDistance) closestRegion = r;
        }

        return closestRegion;
    }

    public static EstateRegion getRegionFromUUID(UUID regionUniqueID) {
        return ESTATE_REGIONS.stream().filter(region -> region.getUniqueID().equals(regionUniqueID)).findFirst().orElse(null);
    }

    public static void initRegions() throws SQLException {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM ESTATE_REGIONS");
        while (result.next()) ESTATE_REGIONS.add(new EstateRegion(UUID.fromString(result.getString("uuid"))));
    }

}