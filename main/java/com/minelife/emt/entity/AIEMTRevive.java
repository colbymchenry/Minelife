package com.minelife.emt.entity;

import com.minelife.Minelife;
import com.minelife.emt.ModEMT;
import com.minelife.emt.ServerProxy;
import com.minelife.police.ModPolice;
import com.minelife.police.server.Prison;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.client.render.Vector;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.List;
import java.util.Objects;

public class AIEMTRevive extends EntityAIBase {

    private double moveSpeed = 1.8;
    private int followRange = 20, arrestRange = 2, tazeRange = 6;
    private int pathAttempt = 0;
    private EntityEMT entity;
    private int inWater = 0;
    private BlockPos previousPosition;

    public AIEMTRevive(EntityEMT cop) {
        this.entity = cop;
        setMutexBits(7);
    }

    // TODO: Stop EMT from running away from player

    @Override
    public boolean shouldContinueExecuting() {
        if ((this.entity.getRevivingPlayer() != null && isAlreadyBeingRevived((EntityPlayer) entity.getAttackTarget())) || (this.entity.getAttackTarget() != null && ModPolice.isUnconscious((EntityPlayer) entity.getAttackTarget())
                && !isAlreadyBeingRevived((EntityPlayer) entity.getAttackTarget()))) {
            // if does not have arrested player go to player that we are attempting to arrest

            previousPosition = this.entity.getPosition();
            if (this.entity.getPosition().getDistance(previousPosition.getX(), previousPosition.getY(), previousPosition.getZ()) < 1 && this.entity.getDistance(this.entity.getAttackTarget()) > 5) {
                this.pathAttempt++;
                if (this.pathAttempt > 100) {
                    this.entity.setPosition(entity.getAttackTarget().posX, entity.getAttackTarget().posY, entity.getAttackTarget().posZ);
                    this.pathAttempt = 0;
                }
            }

            if (getClosestForPathTarget() == null) {
                this.pathAttempt++;

                if (this.pathAttempt > 100) {
                    this.entity.setPosition(entity.getAttackTarget().posX, entity.getAttackTarget().posY, entity.getAttackTarget().posZ);
                    this.pathAttempt = 0;
                }
            } else {
//                this.entity.getNavigator().setPath(getClosestForPathTarget(), moveSpeed);

                if (this.entity.isInWater()) {
                    this.inWater++;

                    if (this.inWater > 50) {
                        this.entity.setPosition(entity.getAttackTarget().posX, entity.getAttackTarget().posY, entity.getAttackTarget().posZ);
                        this.pathAttempt = 0;
                        this.inWater = 0;
                    }
                }
            }

            if (entity.getAttackTarget().getPosition().getDistance((int) entity.posX, (int) entity.posY, (int) entity.posZ) < 3) {

                this.entity.getLookHelper().setLookPosition(this.entity.getRevivingPlayer().posX, this.entity.getRevivingPlayer().posY, this.entity.getRevivingPlayer().posZ, (float) this.entity.getHorizontalFaceSpeed(), (float) this.entity.getVerticalFaceSpeed());

                if (!ModEMT.PLAYERS_BEING_HEALED.containsKey(this.entity.getAttackTarget().getUniqueID())) {
                    ModEMT.PLAYERS_BEING_HEALED.put(this.entity.getAttackTarget().getUniqueID(), System.currentTimeMillis() + (1000L * 21));
                    Minelife.getNetwork().sendToAllAround(new PacketPlaySound("minelife:emt_revive", 1, 1, this.entity.posX, this.entity.posY, this.entity.posZ), new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 10));
                }
            }
        }

        if (this.entity.getRevivingPlayer() != null) return true;

        return this.entity.getAttackTarget() != null && this.entity.getDistance(this.entity.getAttackTarget()) < followRange;
    }

    @Override
    public void startExecuting() {
        this.entity.setRevivingPlayer((EntityPlayer) this.entity.getAttackTarget());
        super.startExecuting();
    }

    @Override
    public boolean shouldExecute() {
        return (this.entity.getRevivingPlayer() != null && !isAlreadyBeingRevived((EntityPlayer) entity.getAttackTarget())) || (this.entity.getAttackTarget() != null && entity.getAttackTarget() instanceof EntityPlayer && ModPolice.isUnconscious((EntityPlayer) entity.getAttackTarget()) && !isAlreadyBeingRevived((EntityPlayer) entity.getAttackTarget()) && !entity.getAttackTarget().getEntityData().hasKey("Tazed"));
    }

    public Path getClosestForPathTarget() {
        if(entity.getNavigator().getPathToPos(entity.getAttackTarget().getPosition()) != null) return entity.getNavigator().getPathToPos(entity.getAttackTarget().getPosition());

        Vector v = new Vector(entity.posX, entity.posY, entity.posZ);
        Vector v1 = new Vector(entity.getAttackTarget().posX, entity.getAttackTarget().posY, entity.getAttackTarget().posZ);
        int distance = (int) v.distance(v1);
        distance = distance > (int) this.entity.getNavigator().getPathSearchRange() ? (int) this.entity.getNavigator().getPathSearchRange() : distance;
        Vector subtracted = v.subtract(v1).normalize();

        Path path = this.entity.getNavigator().getPathToPos(this.entity.getAttackTarget().getPosition());
        if (path == null)
            path = this.entity.getNavigator().getPathToPos(new BlockPos(entity.posX + (-subtracted.getX() * distance), entity.posY, entity.posZ + (-subtracted.getZ() * distance)));

        return path;
    }

    private boolean isAlreadyBeingRevived(EntityPlayer player) {
        return ModEMT.PLAYERS_BEING_HEALED.containsKey(player.getUniqueID()) || ServerProxy.getEMTsForWorld(player.world).stream().filter(entityEMT -> !entityEMT.getUniqueID().equals(entity.getUniqueID()) && entityEMT.getRevivingPlayer() != null && Objects.equals(player.getUniqueID(), entityEMT.getRevivingPlayer().getUniqueID())).findFirst().orElse(null) != null;
    }
}
