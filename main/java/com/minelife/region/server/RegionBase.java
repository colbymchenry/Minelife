package com.minelife.region.server;

import com.minelife.Minelife;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RegionBase {

    protected UUID uuid;
    protected int[] min, max;
    protected String world;

    public UUID getUUID() {
        return uuid;
    }

    public int[] getMin() {
        return this.min;
    }

    public int[] getMax() {
        return this.max;
    }

    public String getWorld() {
        return world;
    }

    public World getEntityWorld() {
        for (WorldServer worldServer : MinecraftServer.getServer().worldServers) {
            if(worldServer.getWorldInfo().getWorldName().equalsIgnoreCase(this.world))
                return worldServer;
        }

        return null;
    }

    public void setMin(int[] coords) throws SQLException {
        this.min = coords;
        Minelife.SQLITE.query("UPDATE regions SET minX='" + coords[0] + "', minY='" + coords[1] + "', minZ='" + coords[2] + "' WHERE uuid='" + this.uuid.toString() + "'");
    }

    public void setMax(int[] coords) throws SQLException {
        this.max = coords;
        Minelife.SQLITE.query("UPDATE regions SET maxX='" + coords[0] + "', maxY='" + coords[1] + "', maxZ='" + coords[2] + "' WHERE uuid='" + this.uuid.toString() + "'");
    }

    public boolean doesContain(int x, int y, int z) {
        return this.min[0] <= x && this.max[0] >= x && this.min[1] <= y && this.max[1] >= y && this.min[2] <= z && this.max[2] >= z;
    }

    public AxisAlignedBB getAxisAlignedBB() {
        return AxisAlignedBB.getBoundingBox(min[0], min[1], min[2], max[0], max[1], max[2]);
    }

}
