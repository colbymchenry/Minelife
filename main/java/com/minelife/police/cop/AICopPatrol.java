package com.minelife.police.cop;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.drugs.ModDrugs;
import com.minelife.guns.ModGuns;
import com.minelife.police.ChargeType;
import com.minelife.police.ModPolice;
import com.minelife.police.Prisoner;
import com.minelife.police.server.Prison;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.client.render.Vector;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.List;
import java.util.Objects;

public class AICopPatrol extends EntityAIBase {

    private EntityCop cop;


    public AICopPatrol(EntityCop cop) {
        this.cop = cop;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (cop.isCarryingPlayer() || cop.getAttackTarget() == null || cop.isTargetArrested()) {
            cop.setPositionAndUpdate(cop.getSpawnPoint().getX() + 0.5, cop.getSpawnPoint().getY() + 0.5, cop.getSpawnPoint().getZ() + 0.5);
            return false;
        }

        if (cop.chargesForTarget.isEmpty()) {
            if (Prisoner.isPrisoner(cop.getAttackTarget().getUniqueID())) {
                cop.chargesForTarget.addAll(Prisoner.getPrisoner(cop.getAttackTarget().getUniqueID()).getCharges());
            }

            if (targetHolding(ModGuns.itemGun)) {
                cop.chargesForTarget.add(ChargeType.POSSESSION_OF_UNLAWFUL_FIREARM);
            } else if (targetHolding(ModGuns.itemDynamite)) {
                cop.chargesForTarget.add(ChargeType.POSSESSION_OF_UNLAWFUL_FIREARM);
            } else if (targetHolding(ModDrugs.itemJoint)) {
                cop.chargesForTarget.add(ChargeType.POSSESSION_OF_MARIJUANA);
            }

            if (Objects.equals(cop.getAttackTarget().getUniqueID(), cop.getKillerPlayer())) {
                cop.chargesForTarget.add(ChargeType.MURDER);
            }

            if (Prison.getPrison(cop.getAttackTarget().getPosition()) != null && !Prisoner.isPrisoner(cop.getAttackTarget().getUniqueID())) {
                cop.chargesForTarget.add(ChargeType.PRISON_BREAK);
            }

            if(cop.getLastDamageSource() != null && cop.getLastDamageSource().getTrueSource() instanceof EntityPlayer) {
                cop.chargesForTarget.add(ChargeType.ASSAULT);
            }


        }

        if (cop.getNavigator().noPath()) {
            Path path = cop.getNavigator().getPathToEntityLiving(cop.getAttackTarget());
            if (path == null) return false;
            else cop.getNavigator().setPath(path, 1.8);
        }

        double distance = cop.getDistance(cop.getAttackTarget());

        if (distance < 2) {
            // handcuff
            if (ModPolice.isUnconscious((EntityPlayer) cop.getAttackTarget())) {
                ModPolice.setUnconscious((EntityPlayer) cop.getAttackTarget(), false, false);
                cop.getAttackTarget().getEntityData().setBoolean("Tazed", false);
                cop.getAttackTarget().startRiding(cop);
            }

        } else if (distance < 4) {
            // taze
            if (!ModPolice.isUnconscious((EntityPlayer) cop.getAttackTarget())) {

                ModPolice.setUnconscious((EntityPlayer) cop.getAttackTarget(), true, true);
                cop.getAttackTarget().getEntityData().setBoolean("Tazed", true);
                Minelife.getNetwork().sendToAllAround(new PacketPlaySound("minelife:tazer", 1, 1), new NetworkRegistry.TargetPoint(cop.dimension, cop.posX, cop.posY, cop.posZ, 10));
            }
        } else {
            cop.getNavigator().tryMoveToEntityLiving(cop.getAttackTarget(), 1.8);
        }

        return true;
    }

    @Override
    public void startExecuting() {
        cop.chargesForTarget.clear();
        cop.getNavigator().tryMoveToEntityLiving(cop.getAttackTarget(), 1.8);
    }

    @Override
    public boolean shouldExecute() {
        return cop.getAttackTarget() != null
                && !Prisoner.isPrisoner(cop.getAttackTarget().getUniqueID())
                && !cop.isCarryingPlayer()
                && !cop.isTargetArrested()
                && cop.getEntitySenses().canSee(cop.getAttackTarget());
    }

    @Override
    public void resetTask() {
        if (cop.getRidingEntity() != null)
            cop.getRidingEntity().dismountRidingEntity();

        cop.setAttackTarget(null);
        cop.setChasingPlayer(null);
        cop.getNavigator().clearPath();
    }

    public boolean targetHolding(Item item) {
        if (cop.getAttackTarget() != null && cop.getAttackTarget() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) cop.getAttackTarget();
            return player.getHeldItem(EnumHand.MAIN_HAND).getItem() == item || player.getHeldItem(EnumHand.OFF_HAND).getItem() == item;
        }
        return false;
    }

}
