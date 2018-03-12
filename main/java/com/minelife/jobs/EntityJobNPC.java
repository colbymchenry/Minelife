package com.minelife.jobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class EntityJobNPC extends EntityCreature {

    protected int npcType = 0;

    public EntityJobNPC(World world) {
        super(world);
        this.tasks.taskEntries.clear();
        this.targetTasks.taskEntries.clear();
        this.tasks.addTask(0, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
    }

    @Override
    public ItemStack getHeldItem() {
        return new ItemStack(Items.iron_hoe);
    }

    @Override
    protected boolean isMovementCeased() {
        return true;
    }

    @Override
    protected void collideWithNearbyEntities() {
    }

    @Override
    protected void collideWithEntity(Entity p_82167_1_) {
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
        return EnumChatFormatting.YELLOW + "Farmer";
    }

}
