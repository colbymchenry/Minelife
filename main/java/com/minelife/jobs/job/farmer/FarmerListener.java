package com.minelife.jobs.job.farmer;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.essentials.ModEssentials;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.job.miner.MinerHandler;
import com.minelife.jobs.server.CommandJob;
import com.minelife.jobs.server.commands.CommandNPC;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.PlayerHelper;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class FarmerListener {

    private static List<UpdateCrop> cropUpdates = Lists.newArrayList();

    @SubscribeEvent
    public void onBreak(BlockEvent.HarvestDropsEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getHarvester();
        World world = event.getWorld();
        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();

        // TODO: Implement sugar cane and pumpkins and water melons
        if(!(world.getBlockState(event.getPos()) instanceof BlockCrops) &&
                block != Blocks.WHEAT && block != Blocks.CARROTS && block != Blocks.POTATOES) return;

        if(player == null) return;

        if(!FarmerHandler.INSTANCE.isProfession(player)) return;

        if(event.isCanceled()) return;

        int level = FarmerHandler.INSTANCE.getLevel(player);
        BlockCrops blockCrop = (BlockCrops) world.getBlockState(event.getPos()).getBlock();
        int xp = FarmerHandler.INSTANCE.getXPForBlock(blockCrop);

        if(xp < 1) return;

        if(FarmerHandler.INSTANCE.doTripleDrop(player) ||
                FarmerHandler.INSTANCE.greenTerraMap.containsKey(player.getUniqueID())) {
            event.getDrops().forEach(itemStack -> itemStack.setCount(itemStack.getCount() * 3));
        }

        if(blockCrop.getMaxAge() == blockCrop.getMetaFromState(world.getBlockState(event.getPos()))) {
            cropUpdates.add(new UpdateCrop(world, player, System.currentTimeMillis() + 500, world.getBlockState(event.getPos()), blockCrop, event.getPos()));
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



    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickItem event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        if (player.getHeldItemMainhand().getItem() != Items.WOODEN_HOE &&
                player.getHeldItemMainhand().getItem() != Items.STONE_HOE &&
                player.getHeldItemMainhand().getItem() != Items.IRON_HOE &&
                player.getHeldItemMainhand().getItem() != Items.GOLDEN_HOE &&
                player.getHeldItemMainhand().getItem() != Items.DIAMOND_HOE) {
            return;
        }

        if (!FarmerHandler.INSTANCE.isProfession(player)) return;

        FarmerHandler.INSTANCE.applyGreenTerra(player, !(event.getWorld().getBlockState(event.getPos()) instanceof BlockCrops) &&
                event.getWorld().getBlockState(event.getPos()).getBlock() != Blocks.WHEAT);
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        if (player.getHeldItemMainhand().getItem() != Items.WOODEN_HOE &&
                player.getHeldItemMainhand().getItem() != Items.STONE_HOE &&
                player.getHeldItemMainhand().getItem() != Items.IRON_HOE &&
                player.getHeldItemMainhand().getItem() != Items.GOLDEN_HOE &&
                player.getHeldItemMainhand().getItem() != Items.DIAMOND_HOE) {
            return;
        }

        if (!FarmerHandler.INSTANCE.isProfession(player)) return;

        if(!(event.getWorld().getBlockState(event.getPos()) instanceof BlockCrops) ||
                event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.GRASS) return;

        FarmerHandler.INSTANCE.applyGreenTerra(player, !(event.getWorld().getBlockState(event.getPos()) instanceof BlockCrops) &&
                event.getWorld().getBlockState(event.getPos()).getBlock() != Blocks.WHEAT);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        final List<UUID> toRemove = Lists.newArrayList();

        Iterator<UpdateCrop> cropIterator = cropUpdates.iterator();
        while(cropIterator.hasNext()) {
            UpdateCrop update = cropIterator.next();
            if(System.currentTimeMillis() >= update.updateTime) {
                update.world.setBlockState(update.pos, update.blockState.withProperty(BlockCrops.AGE,
                        FarmerHandler.INSTANCE.replantStage(update.player, update.blockCrops.getMaxAge())), 2);
                cropIterator.remove();
            }
        }

        FarmerHandler.INSTANCE.greenTerraMap.forEach((player, duration) -> {
            if (System.currentTimeMillis() > duration) toRemove.add(player);
        });

        toRemove.forEach(player -> {
            if (PlayerHelper.getPlayer(player) != null)
                CommandJob.sendMessage(PlayerHelper.getPlayer(player), EnumJob.FARMER, "Green Terra deactivated.");
            FarmerHandler.INSTANCE.greenTerraMap.remove(player);
        });

        toRemove.clear();
        FarmerHandler.INSTANCE.greenTerraCooldownMap.forEach((player, duration) -> {
            if (System.currentTimeMillis() > duration) toRemove.add(player);
        });

        toRemove.forEach(player -> {
            if (PlayerHelper.getPlayer(player) != null) {
                Minelife.getNetwork().sendTo(new PacketPlaySound("minecraft:entity.player.levelup", 1, 1), PlayerHelper.getPlayer(player));
                CommandJob.sendMessage(PlayerHelper.getPlayer(player), EnumJob.FARMER, "Green Terra is now available.");
            }
            FarmerHandler.INSTANCE.greenTerraCooldownMap.remove(player);
        });
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (FarmerHandler.INSTANCE.greenTerraMap.containsKey(player.getUniqueID()))
            FarmerHandler.INSTANCE.greenTerraMap.remove(player.getUniqueID());
    }

    class UpdateCrop {
        EntityPlayerMP player;
        World world;
        long updateTime;
        IBlockState blockState;
        BlockCrops blockCrops;
        BlockPos pos;

        public UpdateCrop(World world, EntityPlayerMP player, long updateTime, IBlockState blockState, BlockCrops blockCrops, BlockPos pos) {
            this.world = world;
            this.player = player;
            this.updateTime = updateTime;
            this.blockState = blockState;
            this.blockCrops = blockCrops;
            this.pos = pos;
        }
    }

}
