package com.minelife.jobs;

import com.google.common.collect.Maps;
import com.minelife.MLItems;
import com.minelife.Minelife;
import com.pam.harvestcraft.ItemRegistry;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.Map;

public class EntityJobNPC extends EntityCreature {

    private static Map<Integer, ItemStack> heldItems = Maps.newHashMap();

    static {
        heldItems.put(-1, new ItemStack(Items.apple));
        heldItems.put(0, new ItemStack(Items.iron_hoe));
        heldItems.put(1, new ItemStack(Items.fishing_rod));
        heldItems.put(2, new ItemStack(Items.iron_pickaxe));
        heldItems.put(3, new ItemStack(Items.bow));
        heldItems.put(4, new ItemStack(ItemRegistry.baconcheeseburgerItem));
        heldItems.put(5, new ItemStack(Items.iron_axe));
        heldItems.put(6, new ItemStack(MLItems.ticket));
    }

    public EntityJobNPC(World world) {
        this(world, 0);
    }

    public EntityJobNPC(World world, int profession) {
        super(world);
        this.setProfession(profession);
        this.tasks.taskEntries.clear();
        this.targetTasks.taskEntries.clear();
        this.tasks.addTask(0, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
    }

    public void setProfession(int profession) {
        this.dataWatcher.updateObject(16, profession);
    }

    public int getProfession() {
        return dataWatcher.getWatchableObjectInt(16);
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
    public ItemStack getHeldItem() {
        return heldItems.containsKey(this.getProfession()) ? heldItems.get(this.getProfession()) : heldItems.get(-1);
    }

    @Override
    protected boolean isMovementCeased() {
        return true;
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
    public boolean hasCustomNameTag() {
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
                return EnumChatFormatting.YELLOW + "Farmer";
            case 1:
                return EnumChatFormatting.AQUA + "Fisherman";
            case 2:
                return EnumChatFormatting.GOLD + "Miner";
            case 3:
                return EnumChatFormatting.RED + "Bounty Hunter";
            case 4:
                return EnumChatFormatting.GREEN + "Restaurateur";
            case 5:
                return EnumChatFormatting.DARK_GREEN + "Lumberjack";
            case 6:
                return EnumChatFormatting.BLUE + "Police";
        }

        return "";
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, 0);
    }

}
