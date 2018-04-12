package com.minelife.jobs;

import com.google.common.collect.Maps;
import com.minelife.guns.ModGuns;
import com.pam.harvestcraft.item.ItemRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.Map;

public class EntityJobNPC extends EntityCreature {

    private static final DataParameter<Integer> DATA_PROFESSION = EntityDataManager.createKey(EntityJobNPC.class, DataSerializers.VARINT);

    private static Map<Integer, ItemStack> heldItems = Maps.newHashMap();

    static {
        heldItems.put(-1, new ItemStack(Items.APPLE));
        heldItems.put(0, new ItemStack(Items.IRON_HOE));
        heldItems.put(1, new ItemStack(Items.FISHING_ROD));
        heldItems.put(2, new ItemStack(Items.IRON_PICKAXE));
        heldItems.put(3, new ItemStack(Items.BOW));
        heldItems.put(4, new ItemStack(ItemRegistry.baconcheeseburgerItem));
        heldItems.put(5, new ItemStack(Items.IRON_AXE));
        heldItems.put(6, new ItemStack(ModGuns.itemGun, 1, 1));
    }

    public EntityJobNPC(World world) {
        this(world, 0);
    }

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
        return heldItems.containsKey(this.getProfession()) ? heldItems.get(this.getProfession()) : heldItems.get(-1);
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
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
        switch (this.getProfession()) {
            case 0:
                return TextFormatting.YELLOW + "Farmer";
            case 1:
                return TextFormatting.AQUA + "Fisherman";
            case 2:
                return TextFormatting.GOLD + "Miner";
            case 3:
                return TextFormatting.RED + "Bounty Hunter";
            case 4:
                return TextFormatting.GREEN + "Restaurateur";
            case 5:
                return TextFormatting.DARK_GREEN + "Lumberjack";
            case 6:
                return TextFormatting.BLUE + "Police";
        }

        return "";
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(DATA_PROFESSION, 0);
    }

}