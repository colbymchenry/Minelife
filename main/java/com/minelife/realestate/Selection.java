package com.minelife.realestate;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Selection {

    private Vec3 pos1, pos2;
    public World world;

    public Selection setPos1(int x, int y, int z) {
        pos1 = Vec3.createVectorHelper(x, y, z);
        return this;
    }

    public Selection setPos2(int x, int y, int z) {
        pos2 = Vec3.createVectorHelper(x, y, z);
        return this;
    }

    public Selection setWorld(World world) {
        this.world = world;
        return this;
    }

    public Vec3 getMin() {
        if (pos1 == null || pos2 == null) return null;
        return Vec3.createVectorHelper(Math.min(pos1.xCoord, pos2.xCoord), Math.min(pos1.yCoord, pos2.yCoord), Math.min(pos1.zCoord, pos2.zCoord));
    }

    public Vec3 getMax() {
        if (pos1 == null || pos2 == null) return null;
        return Vec3.createVectorHelper(Math.max(pos1.xCoord, pos2.xCoord), Math.max(pos1.yCoord, pos2.yCoord), Math.max(pos1.zCoord, pos2.zCoord));
    }

    public boolean isComplete() {
        return pos1 != null && pos2 != null;
    }

    public boolean contains(Selection selection) {
        AxisAlignedBB b = AxisAlignedBB.getBoundingBox(getMin().xCoord, getMin().yCoord, getMin().zCoord, getMax().xCoord, getMax().yCoord, getMax().zCoord);
        AxisAlignedBB b1 = AxisAlignedBB.getBoundingBox(selection.getMin().xCoord, selection.getMin().yCoord, selection.getMin().zCoord,
                selection.getMax().xCoord, selection.getMax().yCoord, selection.getMax().zCoord);
        return world.getWorldInfo().getWorldName().equals(selection.world.getWorldInfo().getWorldName())
                && b.isVecInside(Vec3.createVectorHelper(b1.minX, b1.minY, b1.minZ))
                && b.isVecInside(Vec3.createVectorHelper(b1.maxX, b1.maxY, b1.maxZ));
    }

}