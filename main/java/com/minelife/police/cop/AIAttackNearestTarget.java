package com.minelife.police.cop;

import com.minelife.police.ModPolice;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;

public class AIAttackNearestTarget extends EntityAINearestAttackableTarget {

    public AIAttackNearestTarget(EntityCreature creature, Class classTarget, boolean checkSight) {
        super(creature, classTarget, checkSight);
    }

    @Override
    public boolean shouldContinueExecuting() {
        if(this.taskOwner.getAttackTarget() != null && this.taskOwner.getAttackTarget() instanceof EntityPlayer && ModPolice.isUnconscious((EntityPlayer) this.taskOwner.getAttackTarget())) {
            return false;
        }

        return super.shouldContinueExecuting();
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
//        taskOwner.setAttackTarget(null);
    }

    @Override
    public boolean shouldExecute() {
        return ((EntityCop) taskOwner).prison != null && super.shouldExecute();
    }


}
