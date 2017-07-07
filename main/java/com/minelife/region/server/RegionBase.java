package com.minelife.region.server;

import com.minelife.Minelife;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.sql.SQLException;
import java.util.UUID;

public class RegionBase {

    protected UUID regionUniqueID;
    protected int[] min, max;
    protected String world;

    public UUID getUniqueID() {
        return regionUniqueID;
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

    @SideOnly(Side.SERVER)
    public World getEntityWorld() {
        for (WorldServer worldServer : MinecraftServer.getServer().worldServers) {
            if(worldServer.getWorldInfo().getWorldName().equalsIgnoreCase(this.world))
                return worldServer;
        }

        return null;
    }

    public void setMin(int[] coords) throws SQLException {
        this.min = coords;
        Minelife.SQLITE.query("UPDATE regions SET minX='" + coords[0] + "', minY='" + coords[1] + "', minZ='" + coords[2] + "' WHERE regionUniqueID='" + this.regionUniqueID.toString() + "'");
    }

    public void setMax(int[] coords) throws SQLException {
        this.max = coords;
        Minelife.SQLITE.query("UPDATE regions SET maxX='" + coords[0] + "', maxY='" + coords[1] + "', maxZ='" + coords[2] + "' WHERE regionUniqueID='" + this.regionUniqueID.toString() + "'");
    }

    public boolean doesContain(int x, int y, int z) {
        return this.min[0] <= x && this.max[0] >= x && this.min[1] <= y && this.max[1] >= y && this.min[2] <= z && this.max[2] >= z;
    }

    public AxisAlignedBB getAxisAlignedBB() {
        return AxisAlignedBB.getBoundingBox(min[0], min[1], min[2], max[0], max[1], max[2]);
    }

    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, regionUniqueID.toString());
        buf.writeInt(min[0]);
        buf.writeInt(min[1]);
        buf.writeInt(min[2]);
        buf.writeInt(max[0]);
        buf.writeInt(max[1]);
        buf.writeInt(max[2]);
        ByteBufUtils.writeUTF8String(buf, world);
    }

    public static RegionBase fromBytes(ByteBuf buf)
    {
        RegionBase region = new RegionBase();
        region.regionUniqueID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        region.min = new int[]{buf.readInt(), buf.readInt(), buf.readInt()};
        region.max = new int[]{buf.readInt(), buf.readInt(), buf.readInt()};
        region.world = ByteBufUtils.readUTF8String(buf);
        return region;
    }

}
