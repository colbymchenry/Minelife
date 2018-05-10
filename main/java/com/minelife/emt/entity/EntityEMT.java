package com.minelife.emt.entity;

import com.minelife.emt.ModEMT;
import com.minelife.police.ModPolice;
import com.minelife.util.NumberConversions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EntityEMT extends EntityVillager {

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
//        this.tasks.addTask(2, new AIWatchBeingRevived(this));
        this.tasks.addTask(2, new AIEMTRevive(this));
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

    private EntityPlayer getRevivingPlayer() {
        return revivingPlayer;
    }

    public void setRevivingPlayer(EntityPlayer revivingPlayer) {
        this.revivingPlayer = revivingPlayer;
        this.setAttackTarget(revivingPlayer);
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

    public BlockPos getRandomSpawnPoint() {
        if (ModEMT.getConfig().getStringList("EMTSpawnPoints") == null) return getPosition();
        List<String> spawnPoints = ModPolice.getConfig().getStringList("EMTSpawnPoints");
        if(spawnPoints.size() < 1) return getPosition();
        String s = spawnPoints.get(getEntityWorld().rand.nextInt(spawnPoints.size()));
        String[] data = s.split(",");
        return new BlockPos(NumberConversions.toInt(data[0]), NumberConversions.toInt(data[1]), NumberConversions.toInt(data[2]));
    }


}
