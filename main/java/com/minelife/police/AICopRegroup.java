package com.minelife.police;

import com.minelife.util.NumberConversions;
import com.minelife.util.client.render.Vector;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class AICopRegroup extends EntityAIBase {

    private double moveSpeed = 1.8;
    private BlockPos regroupPos;
    private EntityCop entity;
    private int pathAttempt = 0, inWater = 0;

    public AICopRegroup(EntityCop cop) {
        this.entity = cop;
        setMutexBits(7);
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (regroupPos == null) return false;

        if (shouldExecute()) {

            if(this.entity.isInWater()) {
                inWater++;

                if(inWater > 50) {
                    this.entity.setPosition(regroupPos.getX(), regroupPos.getY(), regroupPos.getZ());
                    this.pathAttempt = 0;
                    this.inWater = 0;
                    return false;
                }
            }

            if (this.getClosestForPathRegroup() != null) {
                this.entity.getNavigator().setPath(getClosestForPathRegroup(), moveSpeed);
            } else {
                this.pathAttempt++;

                if (this.pathAttempt > 100) {
                    this.entity.setPosition(regroupPos.getX(), regroupPos.getY(), regroupPos.getZ());
                    this.pathAttempt = 0;
                }
            }
        }

        double distance = entity.getPosition().getDistance(regroupPos.getX(), regroupPos.getY(), regroupPos.getZ());
        if (distance < 2) regroupPos = null;
        return this.entity.getAttackTarget() == null && !hasArrestedPlayer() && getClosestForPathRegroup() != null;
    }

    @Override
    public boolean shouldExecute() {
        return !hasArrestedPlayer() && regroupPos != null;
    }

    public boolean hasArrestedPlayer() {
        return !this.entity.getPassengers().isEmpty();
    }

    public Path getClosestForPathRegroup() {
        if (regroupPos == null) return null;
        Vector v = new Vector(entity.posX, entity.posY, entity.posZ);
        Vector v1 = new Vector(regroupPos.getX(), regroupPos.getY(), regroupPos.getZ());
        int distance = (int) v.distance(v1);
        distance = distance > (int) this.entity.getNavigator().getPathSearchRange() ? (int) this.entity.getNavigator().getPathSearchRange() : distance;
        Vector subtracted = v.subtract(v1).normalize();

        Path path = this.entity.getNavigator().getPathToPos(regroupPos);
        if (path == null) {
            path = this.entity.getNavigator().getPathToPos(new BlockPos(entity.posX + (-subtracted.getX() * distance), entity.posY, entity.posZ + (-subtracted.getZ() * distance)));
        }
        return path;
    }

    public void setRegroupPos(BlockPos regroupPos) {
        this.regroupPos = regroupPos;
    }

    public BlockPos getRandomRegroupPos() {
        if (!ModPolice.getConfig().contains("regroup")) return null;
        List<String> regroups = ModPolice.getConfig().getStringList("regroup");
        String s = regroups.get(entity.getEntityWorld().rand.nextInt(regroups.size()));
        String[] data = s.split(",");
        return new BlockPos(NumberConversions.toInt(data[0]), NumberConversions.toInt(data[1]), NumberConversions.toInt(data[2]));
    }

}
