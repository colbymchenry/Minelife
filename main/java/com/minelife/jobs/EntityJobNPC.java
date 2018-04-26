package com.minelife.jobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.apache.commons.lang3.text.WordUtils;

public class EntityJobNPC extends EntityCreature {

    private static final DataParameter<Integer> DATA_PROFESSION = EntityDataManager.createKey(EntityJobNPC.class, DataSerializers.VARINT);

    public EntityJobNPC(World world) {
        this(world, 0);
    }

    // TODO: Does not face correct direction or follow players

    public EntityJobNPC(World world, int profession) {
        super(world);
        this.setProfession(profession);
        this.tasks.taskEntries.clear();
        this.targetTasks.taskEntries.clear();
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
    }

    public void setProfession(int profession) {
        this.getDataManager().set(DATA_PROFESSION, profession);
    }

    public int getProfession() {
        return this.getDataManager().get(DATA_PROFESSION);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        this.setProfession(tagCompound.getInteger("Profession"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("Profession", this.getProfession());
    }

    @Override
    public ItemStack getHeldItemMainhand() {
        return EnumJob.values()[this.getProfession()].heldStack;
    }

    @Override
    protected boolean isMovementBlocked() {
        return false;
    }

    @Override
    protected void dealFireDamage(int amount) {

    }

    @Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount) {

    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void collideWithNearbyEntities() {
    }

    @Override
    protected void collideWithEntity(Entity e) {
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public boolean getAlwaysRenderNameTag() {
        return false;
    }

    @Override
    public String getCustomNameTag() {
        return EnumJob.values()[this.getProfession()].coloredName +
                WordUtils.capitalizeFully(EnumJob.values()[this.getProfession()].name().replace("_", " "));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(DATA_PROFESSION, 0);
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (EnumJob.values()[getProfession()].getHandler() != null && hand == EnumHand.MAIN_HAND)
            EnumJob.values()[getProfession()].getHandler().onEntityRightClick(player);
        return super.processInteract(player, hand);
    }

}