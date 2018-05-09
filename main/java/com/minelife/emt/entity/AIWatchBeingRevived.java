package com.minelife.emt.entity;

import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;

public class AIWatchBeingRevived extends EntityAIBase {
    protected EntityEMT entity;
    /**
     * The closest entity which is being watched by this one.
     */
    protected EntityPlayer closestEntity;

    public AIWatchBeingRevived(EntityEMT entityIn) {
        this.entity = entityIn;
        this.setMutexBits(7);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        return this.entity.getRevivingPlayer() != null && this.entity.getDistance(this.entity.getRevivingPlayer()) < 5;
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
        if (this.entity.getRevivingPlayer() != null)
            this.entity.getLookHelper().setLookPosition(this.entity.getRevivingPlayer().posX, this.entity.getRevivingPlayer().posY, this.entity.getRevivingPlayer().posZ, (float) this.entity.getHorizontalFaceSpeed(), (float) this.entity.getVerticalFaceSpeed());
    }
}