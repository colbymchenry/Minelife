package com.minelife.police.cop;

import com.google.common.collect.Lists;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGun;
import com.minelife.guns.item.ItemGun;
import com.minelife.police.ChargeType;
import com.minelife.police.ModPolice;
import com.minelife.police.server.Prison;
import com.minelife.police.server.ServerProxy;
import com.minelife.util.client.render.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class EntityCop extends EntityVillager implements IRangedAttackMob {

    private AIShootPrison aiShoot;
    private EntityPlayer chasingPlayer;
    protected List<ChargeType> chargesForTarget = Lists.newArrayList();
    protected Prison prison;
    private boolean checkedForPrison;

    public EntityCop(World world) {
        super(world);
    }

    public EntityCop(World worldIn, boolean isGuard) {
        this(worldIn);
        if (isGuard)
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModGuns.itemGun, 1, EnumGun.M4A4.ordinal()));
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(1, aiShoot = new AIShootPrison(this, 1.0D, 20, 15.0F));
        this.tasks.addTask(2, new AICopGotoPrison(this));
        this.tasks.addTask(3, new AICopPatrol(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.tasks.addTask(8, new EntityAIOpenDoor(this, true));
        aiShoot.setAttackCooldown(20);
        this.targetTasks.addTask(0, new AIAttackNearestTarget(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if(!checkedForPrison) {
            checkedForPrison = true;
            prison = Prison.getPrison(getPosition());
        }
    }

    public static List<EntityCop> getNearbyPolice(World world, BlockPos pos) {
        return world.getEntitiesWithinAABB(EntityCop.class, new AxisAlignedBB(pos.getX() - 64, pos.getY() - 64, pos.getZ() - 64, pos.getX() + 64, pos.getY() + 64, pos.getZ() + 64));
    }

    public void setChasingPlayer(EntityPlayer chasingPlayer) {
        this.chasingPlayer = chasingPlayer;
    }

    private EntityPlayer getChasingPlayer() {
        return chasingPlayer;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Nullable
    @Override
    public EntityLivingBase getAttackTarget() {
        EntityLivingBase target = getChasingPlayer() != null ? getChasingPlayer() : super.getAttackTarget();
        return target instanceof EntityPlayer && ModPolice.isCop(target.getUniqueID()) ? null : target;
    }

    @Override
    public void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        this.getLookHelper().setLookPosition(this.getAttackTarget().posX, this.getAttackTarget().posY + (double) this.getAttackTarget().getEyeHeight(), this.getAttackTarget().posZ, (float) this.getHorizontalFaceSpeed(), (float) this.getVerticalFaceSpeed());
        if (ItemGun.getClipCount(getHeldItemMainhand()) < 1) {
            ItemGun.reload(this, getHeldItemMainhand());
        } else {
            Vector vector = new Vector(posX, posY + getEyeHeight(), posZ);
            Vector vectorPlayer = new Vector(getAttackTarget().posX, getAttackTarget().posY + 0.5, getAttackTarget().posZ);
            Vector lookVec = vector.subtract(vectorPlayer).normalize();
            Vec3d vec3d = new Vec3d(-lookVec.getX(), -lookVec.getY(), -lookVec.getZ()).addVector(MathHelper.nextDouble(world.rand, -0.08, 0.08), MathHelper.nextDouble(world.rand, -0.08, 0.08), MathHelper.nextDouble(world.rand, -0.08, 0.08));
            ItemGun.fire(this, vec3d, 0);
        }
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {

    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    public boolean isCarryingPlayer() {
        return !getPassengers().isEmpty() && getPassengers().get(0) instanceof EntityPlayer;
    }

    public EntityPlayer getCarryingPlayer() {
        return isCarryingPlayer() ? (EntityPlayer) getPassengers().get(0) : null;
    }

    public boolean isTargetArrested() {
        if (getAttackTarget() == null) return false;
        return ServerProxy.getCopsForWorld(world).stream().filter(cop -> cop.isCarryingPlayer() && cop.getCarryingPlayer().getUniqueID().equals(getAttackTarget().getUniqueID())).findFirst().orElse(null) != null;
    }

    public UUID getKillerPlayer() {
        return getEntityData().hasKey("KillerPlayer") ? UUID.fromString(getEntityData().getString("KillerPlayer")) : null;
    }

    public void setKillerPlayer(UUID playerID) {
        if (playerID == null) {
            getEntityData().removeTag("KillerPlayer");
            return;
        }
        getEntityData().setString("KillerPlayer", playerID.toString());
    }


}
