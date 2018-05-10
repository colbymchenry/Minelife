package com.minelife.emt.entity;

import com.minelife.emt.ModEMT;
import com.minelife.emt.ServerProxy;
import com.minelife.police.ModPolice;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Objects;

public class AIWatchBeingRevived extends EntityAIBase {
    protected EntityEMT entity;
    /**
     * The closest entity which is being watched by this one.
     */
    protected EntityPlayer closestEntity;

    public AIWatchBeingRevived(EntityEMT entityIn) {
        this.entity = entityIn;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        if(this.entity.getAttackTarget() == null) return false;
        if(isAlreadyBeingRevived((EntityPlayer) entity.getAttackTarget())) return false;
        if(!ModPolice.isUnconscious((EntityPlayer) entity.getAttackTarget())) return false;
        if(this.entity.getDistance(this.entity.getAttackTarget()) > 4) return false;
        System.out.println("CALLED");
        return true;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        return shouldExecute();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        updateTask();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        this.closestEntity = null;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask() {
        if (this.entity.getAttackTarget() != null)
            this.entity.getLookHelper().setLookPosition(this.entity.getAttackTarget().posX, this.entity.getAttackTarget().posY, this.entity.getAttackTarget().posZ, (float) this.entity.getHorizontalFaceSpeed(), (float) this.entity.getVerticalFaceSpeed());
    }

    private boolean isAlreadyBeingRevived(EntityPlayer player) {
        return ModEMT.PLAYERS_BEING_HEALED.containsKey(player.getUniqueID()) ||
                ServerProxy.getEMTsForWorld(player.world).stream().filter(
                        entityEMT -> !entityEMT.getUniqueID().equals(entity.getUniqueID())
                                && entityEMT.getAttackTarget() != null
                                && Objects.equals(player.getUniqueID(), entityEMT.getAttackTarget().getUniqueID())).findFirst().orElse(null) != null;
    }
}