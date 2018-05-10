package com.minelife.police.cop;

import com.minelife.drugs.ModDrugs;
import com.minelife.guns.ModGuns;
import com.minelife.police.ModPolice;
import com.minelife.police.server.Prison;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;

public class AIAttackNearestTarget extends EntityAINearestAttackableTarget {

    public AIAttackNearestTarget(EntityCreature creature, Class classTarget, boolean checkSight) {
        super(creature, classTarget, checkSight);
    }

    @Override
    public boolean shouldContinueExecuting() {
        if(this.taskOwner.getAttackTarget() == null) return false;

        if(ModPolice.isCop(this.taskOwner.getAttackTarget().getUniqueID())) {
            taskOwner.setAttackTarget(null);
            ((EntityCop) taskOwner).setChasingPlayer(null);
            taskOwner.getNavigator().clearPath();
            return false;
        }

        if(!targetHolding(ModGuns.itemGun) && !targetHolding(ModDrugs.itemJoint)
                && Prison.getPrison(taskOwner.getAttackTarget().getPosition()) == null) {
            taskOwner.setAttackTarget(null);
            ((EntityCop) taskOwner).setChasingPlayer(null);
            taskOwner.getNavigator().clearPath();
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
        return super.shouldExecute();
    }

    public boolean targetHolding(Item item) {
        if (taskOwner.getAttackTarget() != null && taskOwner.getAttackTarget() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) taskOwner.getAttackTarget();
            return player.getHeldItem(EnumHand.MAIN_HAND).getItem() == item || player.getHeldItem(EnumHand.OFF_HAND).getItem() == item;
        }
        return false;
    }
}
