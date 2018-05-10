package com.minelife.emt.entity;

import com.minelife.Minelife;
import com.minelife.emt.ModEMT;
import com.minelife.emt.ServerProxy;
import com.minelife.police.ModPolice;
import com.minelife.police.server.Prison;
import com.minelife.util.NumberConversions;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.client.render.Vector;
import net.minecraft.entity.SharedMonsterAttributes;
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
        setMutexBits(3);
    }

    // TODO: Stop EMT from running away from player

    @Override
    public boolean shouldContinueExecuting() {
        if (this.entity.getAttackTarget() == null) return false;
        if (isAlreadyBeingRevived((EntityPlayer) entity.getAttackTarget())) return false;
        if (!ModPolice.isUnconscious((EntityPlayer) entity.getAttackTarget())) return false;

        if (entity.getNavigator().noPath()) {
            Path path = findBestPath();
            if (path != null) entity.getNavigator().setPath(path, 1.8);
            else {
                entity.setPositionAndUpdate(entity.getAttackTarget().posX, entity.getAttackTarget().posY + 0.5, entity.getAttackTarget().posZ);
                revivePlayer();
                entity.getNavigator().clearPath();
            }
        }

        return true;
    }

    @Override
    public void resetTask() {
        super.resetTask();
        entity.getNavigator().clearPath();
    }

    @Override
    public void startExecuting() {
        this.entity.setRevivingPlayer((EntityPlayer) this.entity.getAttackTarget());
        super.startExecuting();
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.getAttackTarget() != null
                && ModPolice.isUnconscious((EntityPlayer) entity.getAttackTarget())
                && !isAlreadyBeingRevived((EntityPlayer) entity.getAttackTarget())
                && !entity.getAttackTarget().getEntityData().hasKey("Tazed");
    }

    public Path findBestPath() {
        if (entity.getAttackTarget() == null) return null;

        double maxDistance = entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue();
        double distance = entity.getDistance(entity.getAttackTarget());

        if (distance > maxDistance) {
            Vector v = new Vector(entity.posX, entity.posY, entity.posZ);
            Vector v1 = new Vector(entity.getAttackTarget().posX, entity.getAttackTarget().posY, entity.getAttackTarget().posZ);
            Vector subtracted = v.subtract(v1).normalize();

            return entity.getNavigator().getPathToXYZ(entity.posX + (-subtracted.getX() * maxDistance), entity.posY, entity.posZ + (-subtracted.getZ() * maxDistance));
        }

        return entity.getNavigator().getPathToEntityLiving(entity.getAttackTarget());
    }

    private boolean isAlreadyBeingRevived(EntityPlayer player) {
        return ModEMT.PLAYERS_BEING_HEALED.containsKey(player.getUniqueID()) ||
                ServerProxy.getEMTsForWorld(player.world).stream().filter(
                        entityEMT -> !entityEMT.getUniqueID().equals(entity.getUniqueID())
                                && entityEMT.getAttackTarget() != null
                                && Objects.equals(player.getUniqueID(), entityEMT.getAttackTarget().getUniqueID())).findFirst().orElse(null) != null;
    }

    private void revivePlayer() {
        if (this.entity.getAttackTarget() == null) return;
        ModEMT.PLAYERS_BEING_HEALED.put(this.entity.getAttackTarget().getUniqueID(), System.currentTimeMillis() + (1000L * 21));
        Minelife.getNetwork().sendToAllAround(new PacketPlaySound("minelife:emt_revive", 1, 1, this.entity.posX, this.entity.posY, this.entity.posZ), new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 10));
    }


}
