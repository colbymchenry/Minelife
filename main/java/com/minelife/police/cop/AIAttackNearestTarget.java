package com.minelife.police.cop;

import com.google.common.base.Predicate;
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
        super(creature, classTarget, 0, checkSight, true, (Predicate)null);
    }

    @Override
    public boolean shouldContinueExecuting() {
        if(this.taskOwner.getAttackTarget() == null) return false;
        return super.shouldContinueExecuting();
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        if(this.taskOwner.getAttackTarget() != null && ModPolice.isCop(this.taskOwner.getAttackTarget().getUniqueID())) this.taskOwner.setAttackTarget(null);
        if(!targetHolding(ModGuns.itemGun) && !targetHolding(ModDrugs.itemJoint)) this.taskOwner.setAttackTarget(null);
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
