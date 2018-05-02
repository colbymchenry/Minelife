package com.minelife.police;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class CustomNavigator extends PathNavigateGround {
    private boolean shouldAvoidSun;

    public CustomNavigator(EntityLiving entitylivingIn, World worldIn) {
        super(entitylivingIn, worldIn);
        setEnterDoors(true);
        Field pathFinderField = ReflectionHelper.findField(PathNavigate.class, "pathFinder");
        pathFinderField.setAccessible(true);
        this.nodeProcessor = new CustomWalkNodeProcessor();
        this.nodeProcessor.setCanEnterDoors(true);
    }

    @Override
    public boolean noPath() {
        return super.noPath();
    }

    @Override
    public void onUpdateNavigation() {
        ++this.totalTicks;

        if (this.tryUpdatePath) {
            this.updatePath();
        }

        if (!this.noPath()) {
            if (this.canNavigate()) {
                this.pathFollow();
            } else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()) {
                Vec3d vec3d = this.getEntityPosition();
                Vec3d vec3d1 = this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex());

                if (vec3d.y > vec3d1.y && !this.entity.onGround && MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d1.x) && MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d1.z)) {
                    this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
                }
            }

            this.debugPathFinding();

            if (!this.noPath()) {
                System.out.println("BOM");
                Vec3d vec3d2 = this.currentPath.getPosition(this.entity);
                BlockPos blockpos = (new BlockPos(vec3d2)).down();
                AxisAlignedBB axisalignedbb = this.world.getBlockState(blockpos).getBoundingBox(this.world, blockpos);
                vec3d2 = vec3d2.subtract(0.0D, 1.0D - axisalignedbb.maxY, 0.0D);
                this.entity.getMoveHelper().setMoveTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
            }
        }
    }

    @Override
    protected PathFinder getPathFinder() {
        this.nodeProcessor = new CustomWalkNodeProcessor();
        this.nodeProcessor.setCanEnterDoors(true);
        return new PathFinder(this.nodeProcessor);
    }

    /**
     * If on ground or swimming and can swim
     */
    @Override
    protected boolean canNavigate() {
        System.out.println(this.entity.onGround + "," + (this.getCanSwim() && this.isInLiquid()) + "," + this.entity.isRiding());
        return this.entity.onGround || this.getCanSwim() && this.isInLiquid() || this.entity.isRiding();
    }

    @Override
    protected Vec3d getEntityPosition() {
        return new Vec3d(this.entity.posX, (double) this.getPathablePosY(), this.entity.posZ);
    }

    /**
     * Returns path to given BlockPos
     */
    @Override
    public Path getPathToPos(BlockPos pos) {
        if (this.world.getBlockState(pos).getMaterial() == Material.AIR) {
            BlockPos blockpos;

            for (blockpos = pos.down(); blockpos.getY() > 0 && this.world.getBlockState(blockpos).getMaterial() == Material.AIR; blockpos = blockpos.down()) {
                ;
            }

            if (blockpos.getY() > 0) {
                return super.getPathToPos(blockpos.up());
            }

            while (blockpos.getY() < this.world.getHeight() && this.world.getBlockState(blockpos).getMaterial() == Material.AIR) {
                blockpos = blockpos.up();
            }

            pos = blockpos;
        }

        if (!this.world.getBlockState(pos).getMaterial().isSolid()) {
            System.out.println("HA!");
            return super.getPathToPos(pos);
        } else {
            BlockPos blockpos1;

            for (blockpos1 = pos.up(); blockpos1.getY() < this.world.getHeight() && this.world.getBlockState(blockpos1).getMaterial().isSolid(); blockpos1 = blockpos1.up()) {
                ;
            }

            System.out.println("DAMN");
            return super.getPathToPos(blockpos1);
        }
    }

    /**
     * Returns the path to the given EntityLiving. Args : entity
     */
    @Override
    public Path getPathToEntityLiving(Entity entityIn) {
        return this.getPathToPos(new BlockPos(entityIn));
    }

    /**
     * Gets the safe pathing Y position for the entity depending on if it can path swim or not
     */

    private int getPathablePosY() {
        if (this.entity.isInWater() && this.getCanSwim()) {
            int i = (int) this.entity.getEntityBoundingBox().minY;
            Block block = this.world.getBlockState(new BlockPos(MathHelper.floor(this.entity.posX), i, MathHelper.floor(this.entity.posZ))).getBlock();
            int j = 0;

            while (block == Blocks.FLOWING_WATER || block == Blocks.WATER) {
                ++i;
                block = this.world.getBlockState(new BlockPos(MathHelper.floor(this.entity.posX), i, MathHelper.floor(this.entity.posZ))).getBlock();
                ++j;

                if (j > 16) {
                    return (int) this.entity.getEntityBoundingBox().minY;
                }
            }

            return i;
        } else {
            return (int) (this.entity.getEntityBoundingBox().minY + 0.5D);
        }
    }

    /**
     * Trims path data from the end to the first sun covered block
     */
    @Override
    protected void removeSunnyPath() {
        super.removeSunnyPath();

        if (this.shouldAvoidSun) {
            if (this.world.canSeeSky(new BlockPos(MathHelper.floor(this.entity.posX), (int) (this.entity.getEntityBoundingBox().minY + 0.5D), MathHelper.floor(this.entity.posZ)))) {
                return;
            }

            for (int i = 0; i < this.currentPath.getCurrentPathLength(); ++i) {
                PathPoint pathpoint = this.currentPath.getPathPointFromIndex(i);

                if (this.world.canSeeSky(new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z))) {
                    this.currentPath.setCurrentPathLength(i - 1);
                    return;
                }
            }
        }
    }

    /**
     * Checks if the specified entity can safely walk to the specified location.
     */
    @Override
    protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
        int i = MathHelper.floor(posVec31.x);
        int j = MathHelper.floor(posVec31.z);
        double d0 = posVec32.x - posVec31.x;
        double d1 = posVec32.z - posVec31.z;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 < 1.0E-8D) {
            System.out.println("HOLA");
            return false;
        } else {
            double d3 = 1.0D / Math.sqrt(d2);
            d0 = d0 * d3;
            d1 = d1 * d3;
            sizeX = sizeX + 2;
            sizeZ = sizeZ + 2;

            if (!this.isSafeToStandAt(i, (int) posVec31.y, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) {
                System.out.println("CALLED");
                return false;
            } else {
                sizeX = sizeX - 2;
                sizeZ = sizeZ - 2;
                double d4 = 1.0D / Math.abs(d0);
                double d5 = 1.0D / Math.abs(d1);
                double d6 = (double) i - posVec31.x;
                double d7 = (double) j - posVec31.z;

                if (d0 >= 0.0D) {
                    ++d6;
                }

                if (d1 >= 0.0D) {
                    ++d7;
                }

                d6 = d6 / d0;
                d7 = d7 / d1;
                int k = d0 < 0.0D ? -1 : 1;
                int l = d1 < 0.0D ? -1 : 1;
                int i1 = MathHelper.floor(posVec32.x);
                int j1 = MathHelper.floor(posVec32.z);
                int k1 = i1 - i;
                int l1 = j1 - j;

                while (k1 * k > 0 || l1 * l > 0) {
                    if (d6 < d7) {
                        d6 += d4;
                        i += k;
                        k1 = i1 - i;
                    } else {
                        d7 += d5;
                        j += l;
                        l1 = j1 - j;
                    }

                    if (!this.isSafeToStandAt(i, (int) posVec31.y, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) {
                        System.out.println("CALLED1");
                        return false;
                    }
                }

                return true;
            }
        }
    }

    /**
     * Returns true when an entity could stand at a position, including solid blocks under the entire entity.
     */
    private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d vec31, double p_179683_8_, double p_179683_10_) {
        return true;
//        int i = x - sizeX / 2;
//        int j = z - sizeZ / 2;
//
//        if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_)) {
//            return false;
//        } else {
//            for (int k = i; k < i + sizeX; ++k) {
//                for (int l = j; l < j + sizeZ; ++l) {
//                    double d0 = (double) k + 0.5D - vec31.x;
//                    double d1 = (double) l + 0.5D - vec31.z;
//
//                    if (d0 * p_179683_8_ + d1 * p_179683_10_ >= 0.0D) {
//                        PathNodeType pathnodetype = this.nodeProcessor.getPathNodeType(this.world, k, y - 1, l, this.entity, sizeX, sizeY, sizeZ, true, true);
//
//                        if (pathnodetype == PathNodeType.WATER) {
//                            return false;
//                        }
//
//                        if (pathnodetype == PathNodeType.LAVA) {
//                            return false;
//                        }
//
//                        if (pathnodetype == PathNodeType.OPEN) {
//                            return false;
//                        }
//
//                        pathnodetype = this.nodeProcessor.getPathNodeType(this.world, k, y, l, this.entity, sizeX, sizeY, sizeZ, true, true);
//                        float f = this.entity.getPathPriority(pathnodetype);
//
//                        if (f < 0.0F || f >= 8.0F) {
//                            return false;
//                        }
//
//                        if (pathnodetype == PathNodeType.DAMAGE_FIRE || pathnodetype == PathNodeType.DANGER_FIRE || pathnodetype == PathNodeType.DAMAGE_OTHER) {
//                            return false;
//                        }
//                    }
//                }
//            }
//
//            return true;
//        }
    }

    /**
     * Returns true if an entity does not collide with any solid blocks at the position.
     */
    private boolean isPositionClear(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d p_179692_7_, double p_179692_8_, double p_179692_10_) {
        for (BlockPos blockpos : BlockPos.getAllInBox(new BlockPos(x, y, z), new BlockPos(x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1))) {
            double d0 = (double) blockpos.getX() + 0.5D - p_179692_7_.x;
            double d1 = (double) blockpos.getZ() + 0.5D - p_179692_7_.z;

            if (d0 * p_179692_8_ + d1 * p_179692_10_ >= 0.0D) {
                Block block = this.world.getBlockState(blockpos).getBlock();

                System.out.println(block.getRegistryName().toString());
//                if (!block.isPassable(this.world, blockpos) && block != Blocks.OAK_DOOR) {
//                    return false;
//                }
            }
        }

        return true;
    }

    @Override
    public void setBreakDoors(boolean canBreakDoors) {
        this.nodeProcessor.setCanOpenDoors(canBreakDoors);
    }

    @Override
    public void setEnterDoors(boolean enterDoors) {
        this.nodeProcessor.setCanEnterDoors(enterDoors);
    }

    @Override
    public boolean getEnterDoors() {
        return this.nodeProcessor.getCanEnterDoors();
    }

    @Override
    public void setCanSwim(boolean canSwim) {
        this.nodeProcessor.setCanSwim(canSwim);
    }

    @Override
    public boolean getCanSwim() {
        return this.nodeProcessor.getCanSwim();
    }

    @Override
    public void setAvoidSun(boolean avoidSun) {
        this.shouldAvoidSun = avoidSun;
    }

//    @Override
//    public Path getPathToPos(BlockPos pos)
//    {
//        Field fieldTargetPos = ReflectionHelper.findField(PathNavigate.class, "targetPos");
//        fieldTargetPos.setAccessible(true);
//        try {
//            BlockPos targetPos = (BlockPos) fieldTargetPos.get(this);
//
//
//            if (!this.canNavigate())
//            {
//                return null;
//            }
//            else if (this.currentPath != null && !this.currentPath.isFinished() && pos.equals(targetPos))
//            {
//                return this.currentPath;
//            }
//            else
//            {
//                fieldTargetPos.set(pos);
//                float f = this.getPathSearchRange();
//                this.world.profiler.startSection("pathfind");
//                BlockPos blockpos = new BlockPos(this.entity);
//                int i = (int)(f + 8.0F);
//                ChunkCache chunkcache = new ChunkCache(this.world, blockpos.add(-i, -i, -i), blockpos.add(i, i, i), 0);
//                Path path = this.pathFinder.findPath(chunkcache, this.entity, targetPos, f);
//                this.world.profiler.endSection();
//                return path;
//            }
//
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
}