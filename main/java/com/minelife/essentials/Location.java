package com.minelife.essentials;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class Location {

    private int dimension;
    private double x, y, z;
    private float yaw, pitch;

    public Location(int dimension, double x, double y, double z) {
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location(int dimension, double x, double y, double z, float yaw, float pitch) {
        this(dimension, x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    @SideOnly(Side.SERVER)
    public World getEntityWorld() {
        for (WorldServer worldServer : FMLServerHandler.instance().getServer().worlds) {
            if(worldServer.provider.getDimension() == getDimension()) return worldServer;
        }
        return null;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        buf.writeInt(dimension);
    }

    public static Location fromBytes(ByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        float yaw = buf.readFloat();
        float pitch = buf.readFloat();
        int dimension = buf.readInt();
        return new Location(dimension, x, y, z, yaw, pitch);
    }

}