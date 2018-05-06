package com.minelife.pvplogger;

import com.minelife.realestate.EntityReceptionist;
import com.minelife.util.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityPlayerTracker extends EntityCreature {

    private static final DataParameter<String> DATA_PLAYER_ID = EntityDataManager.createKey(EntityPlayerTracker.class, DataSerializers.STRING);

    long spawnTime;

    public EntityPlayerTracker(World world) {
        super(world);
    }

    private IInventory inventory;

    public EntityPlayerTracker(World worldIn, EntityPlayer player) {
        super(worldIn);
        this.setCustomNameTag(player.getCustomNameTag());
        this.setHealth(player.getHealth());
        this.setPlayerID(player.getUniqueID());
        this.inventory = player.inventory;
        this.setHeldItem(EnumHand.MAIN_HAND, player.getHeldItem(EnumHand.MAIN_HAND));
        this.setHeldItem(EnumHand.OFF_HAND, player.getHeldItem(EnumHand.OFF_HAND));

        ItemStack boots = player.inventory.armorInventory.get(0);
        ItemStack pants = player.inventory.armorInventory.get(1);
        ItemStack chest = player.inventory.armorInventory.get(2);
        ItemStack head = player.inventory.armorInventory.get(3);
        this.setItemStackToSlot(EntityEquipmentSlot.HEAD, head);
        this.setItemStackToSlot(EntityEquipmentSlot.CHEST, chest);
        this.setItemStackToSlot(EntityEquipmentSlot.LEGS, pants);
        this.setItemStackToSlot(EntityEquipmentSlot.FEET, boots);
    }

    public void setPlayerID(UUID playerID) {
        this.getDataManager().set(DATA_PLAYER_ID, playerID.toString());
    }

    public UUID getPlayerID() {
        return UUID.fromString(this.getDataManager().get(DATA_PLAYER_ID));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        spawnTime++;

        if(spawnTime > 1000) {
            ModPVPLogger.playerTrackers.remove(getPlayerID());
            ModPVPLogger.damageMap.remove(getPlayerID());
            getEntityData().setBoolean("DiedNaturally", true);
            setDead();
        }

    }

    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        this.setPlayerID(UUID.fromString(tagCompound.getString("PlayerID")));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setString("PlayerID", this.getPlayerID().toString());
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(DATA_PLAYER_ID, UUID.randomUUID().toString());
    }

    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 2.0D));
        this.tasks.addTask(3, new EntityAITempt(this, 1.25D, Items.WHEAT, false));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
    }

}
