package com.minelife.police;

import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGun;
import com.minelife.guns.item.ItemGun;
import com.minelife.police.server.Prison;
import com.minelife.util.client.render.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
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

public class EntityCop extends EntityVillager implements IRangedAttackMob {

    private AICopRegroup aiRegroup;
    private AICopPolice aiPolice;
    private AIShootPrison aiShoot;
    private EntityPlayer chasingPlayer;

    public EntityCop(World worldIn) {
        super(worldIn);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(1, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(2, aiShoot = new AIShootPrison(this, 1.0D, 20, 15.0F));
        this.tasks.addTask(3, aiPolice = new AICopPolice(this));
        this.tasks.addTask(4, aiRegroup = new AICopRegroup(this));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.tasks.addTask(8, new EntityAIOpenDoor(this, true));
        this.targetTasks.addTask(0, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        aiShoot.setAttackCooldown(20);
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

    public void setChasingPlayer(EntityPlayer chasingPlayer) {
        this.chasingPlayer = chasingPlayer;
    }

    public EntityPlayer getChasingPlayer() {
        return chasingPlayer;
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        this.setEquipmentBasedOnDifficulty(difficulty);
        this.setEnchantmentBasedOnDifficulty(difficulty);
        this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * difficulty.getClampedAdditionalDifficulty());
        return livingdata;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Nullable
    @Override
    public EntityLivingBase getAttackTarget() {
        return getChasingPlayer() != null ? getChasingPlayer() : super.getAttackTarget();
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    public void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        if (Prison.getPrison(this.getPosition()) != null)
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModGuns.itemGun, 1, EnumGun.M4A4.ordinal()));
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
}
