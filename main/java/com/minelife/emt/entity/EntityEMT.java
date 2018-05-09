package com.minelife.emt.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityEMT extends EntityVillager {

    private AIEMTRegroup aiRegroup;
    private AIEMTRevive aiRevive;
    private EntityPlayer revivingPlayer;

    public EntityEMT(World worldIn) {
        super(worldIn);
    }

    // TODO: Game is completely freezing unless there is AI. What the hell?
    @Override
    protected void initEntityAI() {
        this.tasks.taskEntries.clear();
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(1, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(2, aiRegroup = new AIEMTRegroup(this));
        this.tasks.addTask(3, aiRevive = new AIEMTRevive(this));
        this.tasks.addTask(4, new AIWatchBeingRevived(this));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.tasks.addTask(7, new EntityAIOpenDoor(this, true));
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

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    public AIEMTRegroup getRegroupAI() {
        return aiRegroup;
    }

    public AIEMTRevive getReviveAI() {
        return aiRevive;
    }
}
