package com.minelife.police;

import com.minelife.police.server.Prison;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;

public class EntityCop extends EntityVillager {

    private AICopRegroup aiRegroup;
    private AICopPolice aiPolice;

    public EntityCop(World worldIn) {
        super(worldIn);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(1, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(2, aiPolice = new AICopPolice(this));
        this.tasks.addTask(3, aiRegroup = new AICopRegroup(this));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.tasks.addTask(7, new EntityAIOpenDoor(this, true));
        this.targetTasks.addTask(0, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(80.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
    }

    public Prison getNearbyPrison() {
        return Prison.getClosestPrison(getPosition());
    }

    public AICopRegroup getRegroupAI() {
        return aiRegroup;
    }

    public AICopPolice getPoliceAI() {
        return aiPolice;
    }

    public static List<EntityCop> getNearbyPolice(World world, BlockPos pos) {
        return world.getEntitiesWithinAABB(EntityCop.class, new AxisAlignedBB(pos.getX() - 20, pos.getY() - 20, pos.getZ() - 20, pos.getX() + 20, pos.getY() + 20, pos.getZ() + 20));
    }
}
