package com.minelife.police.cop;

import net.minecraft.entity.ai.EntityAIBase;

public class AICopGoHome extends EntityAIBase {

    private EntityCop cop;

    public AICopGoHome(EntityCop cop) {
        this.cop = cop;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if(cop.getAttackTarget() != null) return false;
        if(cop.isCarryingPlayer()) return false;
        if(cop.getDistance(cop.getSpawnPoint().getX(), cop.getSpawnPoint().getY(), cop.getSpawnPoint().getZ()) < 2) return false;
        return true;
    }

    @Override
    public void resetTask() {
        super.resetTask();
        cop.getNavigator().clearPath();
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        cop.getNavigator().tryMoveToXYZ(cop.getSpawnPoint().getX(), cop.getSpawnPoint().getY(), cop.getSpawnPoint().getZ(), 1.0D);
    }

    @Override
    public boolean shouldExecute() {
        if(cop.getAttackTarget() != null) return false;
        if(cop.isCarryingPlayer()) return false;
        if(cop.getSpawnPoint() == null) return false;
        return cop.getDistance(cop.getSpawnPoint().getX(), cop.getSpawnPoint().getY(), cop.getSpawnPoint().getZ()) > 11;
    }
}
