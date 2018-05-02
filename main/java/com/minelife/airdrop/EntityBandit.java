package com.minelife.airdrop;

import com.google.common.collect.Lists;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGun;
import com.minelife.guns.item.ItemGun;
import com.minelife.realestate.EntityReceptionist;
import com.minelife.util.DateHelper;
import com.minelife.util.client.render.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EntityBandit extends EntityMob implements IRangedAttackMob {

    private static final DataParameter<Integer> DATA_SKIN = EntityDataManager.createKey(EntityReceptionist.class, DataSerializers.VARINT);
    private long spawnTime = 0;

    public EntityBandit(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 1.99F);
        this.setSkin(worldIn.rand.nextInt(4));
        this.spawnTime = System.currentTimeMillis();
    }

    protected void initEntityAI() {
        EntityAIShoot aiShoot;
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
        this.tasks.addTask(4, aiShoot = new EntityAIShoot(this, 1.0D, 20, 15.0F));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 30.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
        aiShoot.setAttackCooldown(20);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if ((System.currentTimeMillis() - spawnTime) / 60000L > 60) {
            setDead();
        }
    }

    public void setSkin(int skin) {
        this.getDataManager().set(DATA_SKIN, skin);
    }

    public int getSkin() {
        return this.getDataManager().get(DATA_SKIN);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        this.setSkin(tagCompound.getInteger("Skin"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("Skin", this.getSkin());
    }


    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(DATA_SKIN, 0);
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    public void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        List<EnumGun> validGuns = Lists.newArrayList();
        validGuns.add(EnumGun.AK47);
        validGuns.add(EnumGun.DESERT_EAGLE);
        validGuns.add(EnumGun.M4A4);
        validGuns.add(EnumGun.MAGNUM);
        validGuns.add(EnumGun.BARRETT);

        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModGuns.itemGun, 1, validGuns.get(world.rand.nextInt(validGuns.size())).ordinal()));
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