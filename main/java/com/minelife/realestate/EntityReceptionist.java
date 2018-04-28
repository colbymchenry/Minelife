package com.minelife.realestate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.realestate.network.PacketOpenReceptionistGUI;
import com.minelife.util.StringHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.security.Permission;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityReceptionist extends EntityCreature {

    private static final DataParameter<Integer> DATA_SKIN = EntityDataManager.createKey(EntityReceptionist.class, DataSerializers.VARINT);

    public EntityReceptionist(World world) {
        this(world, "Receptionist", 0);
    }

    public EntityReceptionist(World world, String name, int skin) {
        super(world);
        this.setCustomNameTag(StringHelper.ParseFormatting(name, '&'));
        setSkin(skin);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote)
            if (ModRealEstate.getEstateAt(world, getPosition()) == null) world.removeEntity(this);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
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

//    @Override
//    public ItemStack getHeldItemMainhand() {
//        return EnumJob.values()[this.getProfession()].heldStack;
//    }

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
        return true;
    }

    @Override
    public boolean getAlwaysRenderNameTag() {
        return false;
    }

    @Override
    public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {

    }

    @Override
    public boolean attackable() {
        return false;
    }


    @Override
    public boolean hitByEntity(Entity entityIn) {
        return false;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(DATA_SKIN, 0);
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if(!getEntityWorld().isRemote) {
            Estate estate = ModRealEstate.getEstateAt(getEntityWorld(), getPosition());
            if(estate != null) {
                List<Estate> estates = Lists.newArrayList();
                estates.addAll(estate.getContainingEstates());
                Iterator<Estate> iterator = estates.iterator();
                while(iterator.hasNext()) {
                    Estate e = iterator.next();
                    if (e.getRentPeriod() <= 0 || e.getRentPrice() <= 0) iterator.remove();
                }
                Map<Estate, Set<PlayerPermission>> estatesMap = Maps.newHashMap();
                estates.forEach(e -> {
                    Set<PlayerPermission> perms =  e.getActualRenterPerms();
                    perms.addAll(e.getActualGlobalPerms());
                    estatesMap.put(e, perms);
                });
                Minelife.getNetwork().sendTo(new PacketOpenReceptionistGUI(estatesMap), (EntityPlayerMP) player);
            }
        }
        return super.processInteract(player, hand);
    }

}