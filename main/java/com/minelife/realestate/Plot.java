package com.minelife.realestate;

import com.minelife.Minelife;
import com.minelife.realestate.client.Selection;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

public class Plot implements Comparable<Plot> {

    private static final Set<Plot> PLOTS = new TreeSet<>();

    private AxisAlignedBB bounds;
    private UUID uniqueID;
    private String worldName;

    private Plot() { }

    private Plot(UUID uniqueID) throws SQLException {
        this.uniqueID = uniqueID;
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM plots WHERE uuid = '" + this.uniqueID + "'");
        this.worldName = result.getString("world");
        int i = 2;
        this.bounds = AxisAlignedBB.getBoundingBox(result.getInt(i++), result.getInt(i++), result.getInt(i++), result.getInt(i++), result.getInt(i++), result.getInt(i));
        PLOTS.add(this);
        System.out.println("Plot with UUID=" + this.uniqueID + " added to TreeSet.");
    }

    public static Plot create(Selection selection) throws Exception {
        if (intersectsAnotherRegion(selection)) throw new Exception("Selection intersects an already existing Plot.");
        if (surroundsAnotherRegion(selection)) throw new Exception("Selection contains an already existing Plot.");
        UUID uniqueID = UUID.randomUUID();
        Minelife.SQLITE.query("INSERT INTO plots (uuid, world, minX, minY, minZ, maxX, maxY, maxZ) VALUES (" +
                "'" + uniqueID.toString() + "'," +
                "'" + selection.getWorldName() + "'," +
                "'" + (int) selection.getBounds().minX + "', '" + (int) selection.getBounds().minY + "', '" + (int) selection.getBounds().minZ + "'," +
                "'" + (int) selection.getBounds().maxX + "', '" +  (int) selection.getBounds().maxY  + "', '" + (int) selection.getBounds().maxZ + "')");
        return new Plot(uniqueID);
    }

    public static void delete(Plot plot) throws SQLException {
        Minelife.SQLITE.query("DELETE FROM plots WHERE uuid = '" + plot.uniqueID.toString() + "'");
        PLOTS.remove(plot);
    }

    public static void delete(UUID uniqueID) throws SQLException {
        delete(fromUUID(uniqueID));
    }

    public void delete() throws Exception {
        delete(this);
    }

    public static boolean intersectsAnotherRegion(Selection selection) {
        return PLOTS.stream().anyMatch(plot -> plot.bounds.intersectsWith(selection.getBounds()) && plot.worldName.equals(selection.getWorldName()));
    }

    public static boolean surroundsAnotherRegion(Selection selection) {
        return PLOTS.stream().anyMatch(plot -> {
           Vec3 rMin = Vec3.createVectorHelper(plot.bounds.minX, plot.bounds.minY, plot.bounds.minZ);
           Vec3 rMax = Vec3.createVectorHelper(plot.bounds.maxX, plot.bounds.maxY, plot.bounds.maxZ);
           return selection.getBounds().isVecInside(rMin) && selection.getBounds().isVecInside(rMax);
        });
    }

    private Plot closest;

    public AxisAlignedBB getBounds() {
        return this.bounds;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public Plot getParent() {
        Vec3 min = Vec3.createVectorHelper(this.bounds.minX, this.bounds.minY, this.bounds.minZ);
        Vec3 max = Vec3.createVectorHelper(this.bounds.maxX, this.bounds.maxY, this.bounds.maxZ);
        List<Plot> enclosing = PLOTS.stream().filter(plot -> plot.bounds.isVecInside(min) && plot.bounds.isVecInside(max)).collect(Collectors.toList());
        if (!enclosing.isEmpty()) {
            if (closest == null) closest = enclosing.get(0);
            enclosing.forEach(plot -> {
                Vec3 rMin = Vec3.createVectorHelper(plot.bounds.minX, plot.bounds.minY, plot.bounds.minZ);
                Vec3 rMax = Vec3.createVectorHelper(plot.bounds.maxX, plot.bounds.maxY, plot.bounds.maxZ);
                Vec3 cMin = Vec3.createVectorHelper(closest.bounds.minX, closest.bounds.minY, closest.bounds.minZ);
                Vec3 cMax = Vec3.createVectorHelper(closest.bounds.maxX, closest.bounds.maxY, closest.bounds.maxZ);
                if (rMin.distanceTo(min) + rMax.distanceTo(max) < cMin.distanceTo(min) + cMax.distanceTo(max))
                    closest = plot;
            });
            return closest;
        }
        return null;
    }

    public List<Plot> getChildren() {
        return PLOTS.stream().filter(plot -> plot.getParent().equals(this)).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Plot && ((Plot) obj).worldName.equals(this.worldName) && ((Plot) obj).uniqueID.equals(this.uniqueID) && ((Plot) obj).hasEquivalentBoundsWith(this);
    }

    private boolean hasEquivalentBoundsWith(Plot plot) {
        return this.bounds.minX == plot.bounds.minX &&
                this.bounds.minY == plot.bounds.minY &&
                this.bounds.minZ == plot.bounds.minZ &&
                this.bounds.maxX == plot.bounds.maxX &&
                this.bounds.maxY == plot.bounds.maxY &&
                this.bounds.maxZ== plot.bounds.maxZ;
    }

    @Override
    public int compareTo(Plot other) {
        if (other != null) {
            if (other.getParent() != null && other.getParent().equals(this)) return 1;
            else if (this.getParent() != null && this.getParent().equals(other)) return -1;
        }
        return 0;
    }

    public static void initRegions() throws SQLException {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM plots");
        while (result.next()) PLOTS.add(new Plot(UUID.fromString(result.getString("uuid"))));
    }

    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.bounds.minX);
        buf.writeDouble(this.bounds.minY);
        buf.writeDouble(this.bounds.minZ);
        buf.writeDouble(this.bounds.maxX);
        buf.writeDouble(this.bounds.maxY);
        buf.writeDouble(this.bounds.maxZ);
        ByteBufUtils.writeUTF8String(buf, this.uniqueID.toString());
        ByteBufUtils.writeUTF8String(buf, this.worldName);
    }

    public static Plot fromBytes(ByteBuf buf) {
        Plot plot = new Plot();
        plot.bounds = AxisAlignedBB.getBoundingBox(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
        plot.uniqueID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        plot.worldName = ByteBufUtils.readUTF8String(buf);
        return plot;
    }

    public static Plot fromUUID(UUID uniqueID) {
        return PLOTS.stream().filter(plot -> plot.uniqueID.equals(uniqueID)).findFirst().orElse(null);
    }

}