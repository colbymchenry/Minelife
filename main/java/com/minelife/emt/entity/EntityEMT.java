package com.minelife.emt.entity;

import com.minelife.police.AICopPolice;
import com.minelife.police.AICopRegroup;
import com.minelife.police.EntityCop;
import com.minelife.police.server.Prison;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EntityEMT extends EntityVillager {

    private EntityPlayer revivingPlayer;

    public EntityEMT(World worldIn) {
        super(worldIn);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(1, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(2, new AIEMTRevive(this));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(4, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(5, new EntityAILookIdle(this));
        this.tasks.addTask(5, new EntityAIOpenDoor(this, true));
        this.targetTasks.addTask(0, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(80.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
    }

    public EntityPlayer getRevivingPlayer() {
        return revivingPlayer;
    }

    public void setRevivingPlayer(EntityPlayer revivingPlayer) {
        this.revivingPlayer = revivingPlayer;
    }

    @Nullable
    @Override
    public EntityLivingBase getAttackTarget() {
        return getRevivingPlayer() != null ? getRevivingPlayer() : super.getAttackTarget();
    }
}
