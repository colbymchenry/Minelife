package com.minelife.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class Location {

    private String world;
    private double x, y, z;

    public Location(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
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

    @SideOnly(Side.SERVER)
    public World getEntityWorld() {
        for (WorldServer worldServer : MinecraftServer.getServer().worldServers) {
            if(worldServer.getWorldInfo().getWorldName().equalsIgnoreCase(world)) return worldServer;
        }
        return null;
    }

}
