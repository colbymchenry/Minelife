package com.minelife.jobs.job.farmer;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.essentials.ModEssentials;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.server.CommandJob;
import com.minelife.jobs.server.commands.CommandNPC;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.block.BlockCrops;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FarmerListener {

    @SubscribeEvent
    public void onBreak(BlockEvent.HarvestDropsEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getHarvester();
        World world = event.getWorld();

        if(!(world.getBlockState(event.getPos()) instanceof BlockCrops)) return;

        if(player == null) return;

        if(!FarmerHandler.INSTANCE.isProfession(player)) return;

        FarmerHandler.INSTANCE.getConfig().set("crops", Lists.newArrayList("minelife:hemp_crop;50", "minelife:coca_crop;10", "minecraft:wheat_crop;10"));
        FarmerHandler.INSTANCE.getConfig().save();

        Estate estate = ModRealEstate.getEstateAt(world, event.getPos());
        boolean inFarmZone = estate != null && FarmerHandler.INSTANCE.getConfig().getStringList("zones").contains(String.valueOf(estate.getUniqueID()));

        int level = FarmerHandler.INSTANCE.getLevel(player);
        BlockCrops blockCrop = (BlockCrops) world.getBlockState(event.getPos()).getBlock();
        int xp = FarmerHandler.INSTANCE.getXPForBlock(blockCrop);

        if(xp < 1) return;

        // auto replant
        if(inFarmZone) {
            if (blockCrop.getMaxAge() < world.getBlockState(event.getPos()).getValue(BlockCrops.AGE)) {
                event.setCanceled(true);
                return;
            }
        }

        if(FarmerHandler.INSTANCE.replant(player)) {
            world.setBlockState(event.getPos(), blockCrop.getDefaultState().withProperty(BlockCrops.AGE, FarmerHandler.INSTANCE.getGrowthStage(player)), 2);
        }

        FarmerHandler.INSTANCE.addXP(player.getUniqueID(), xp);
        CommandJob.sendMessage(player, EnumJob.FARMER, "+" + xp);

        if(FarmerHandler.INSTANCE.getLevel(player) > level) {
            Minelife.getNetwork().sendTo(new PacketPlaySound("minelife:level_up", 1, 1), player);

            ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                    new int[]{Color.RED.asRGB(), Color.BLUE.asRGB()}, new int[]{Color.PURPLE.asRGB(), Color.WHITE.asRGB()}).getStack(1);

            EntityFireworkRocket ent = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent);

            ModEssentials.sendTitle(TextFormatting.YELLOW.toString() + TextFormatting.BOLD.toString() + "Level Up!",
                    TextFormatting.YELLOW + "New Level: " + TextFormatting.BLUE + FarmerHandler.INSTANCE.getLevel(player), 5, player);
        }
    }

}
