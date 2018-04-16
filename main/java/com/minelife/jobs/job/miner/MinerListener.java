package com.minelife.jobs.job.miner;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.essentials.ModEssentials;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.server.CommandJob;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.PlayerHelper;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.UUID;

public class MinerListener {

    // TODO: Breaking blocks in protected estate over and over again
    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();

        if (player == null) return;

        if (!MinerHandler.INSTANCE.isProfession(player)) return;

        if (event.isCanceled()) return;

        int level = MinerHandler.INSTANCE.getLevel(player);
        int xp = MinerHandler.INSTANCE.getXPForBlock(event.getState().getBlock());

        if (xp < 1) return;

        MinerHandler.INSTANCE.addXP(player.getUniqueID(), xp);

        if (event.getWorld().getBlockState(event.getPos()).getBlock() != Blocks.STONE)
            CommandJob.sendMessage(player, EnumJob.MINER, "+" + xp);

        if (MinerHandler.INSTANCE.superBreakerMap.containsKey(player.getUniqueID()) || MinerHandler.INSTANCE.doDoubleDrop(player)) {
            NonNullList<ItemStack> drops = NonNullList.create();
            event.getState().getBlock().getDrops(drops, event.getWorld(), event.getPos(), event.getState(), 0);
            drops.forEach(stack -> {
                stack.setCount(stack.getCount() + 1);
                player.dropItem(stack, false);
            });
        }

        if (MinerHandler.INSTANCE.getLevel(player) > level) {
            Minelife.getNetwork().sendTo(new PacketPlaySound("minelife:level_up", 1, 1), player);

            ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                    new int[]{Color.RED.asRGB(), Color.BLUE.asRGB()}, new int[]{Color.PURPLE.asRGB(), Color.WHITE.asRGB()}).getStack(1);

            EntityFireworkRocket ent = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent);

            ModEssentials.sendTitle(TextFormatting.YELLOW.toString() + TextFormatting.BOLD.toString() + "Level Up!",
                    TextFormatting.YELLOW + "New Level: " + TextFormatting.BLUE + MinerHandler.INSTANCE.getLevel(player), 5, player);
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickItem event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        if (player.getHeldItemMainhand().getItem() != Items.WOODEN_PICKAXE &&
                player.getHeldItemMainhand().getItem() != Items.STONE_PICKAXE &&
                player.getHeldItemMainhand().getItem() != Items.IRON_PICKAXE &&
                player.getHeldItemMainhand().getItem() != Items.GOLDEN_PICKAXE &&
                player.getHeldItemMainhand().getItem() != Items.DIAMOND_PICKAXE) {
            return;
        }

        if (!MinerHandler.INSTANCE.isProfession(player)) return;

        MinerHandler.INSTANCE.applySuperBreaker(player);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        final List<UUID> toRemove = Lists.newArrayList();

        MinerHandler.INSTANCE.superBreakerMap.forEach((player, duration) -> {
            if (System.currentTimeMillis() > duration) toRemove.add(player);
        });
        toRemove.forEach(player -> {
            if (PlayerHelper.getPlayer(player) != null)
                CommandJob.sendMessage(PlayerHelper.getPlayer(player), EnumJob.MINER, "Super Breaker deactivated.");
            MinerHandler.INSTANCE.superBreakerMap.remove(player);
        });

        toRemove.clear();
        MinerHandler.INSTANCE.superBreakerCooldownMap.forEach((player, duration) -> {
            if (System.currentTimeMillis() > duration) toRemove.add(player);
        });

        toRemove.forEach(player -> {
            if (PlayerHelper.getPlayer(player) != null)
                CommandJob.sendMessage(PlayerHelper.getPlayer(player), EnumJob.MINER, "Super Breaker is now available.");
            MinerHandler.INSTANCE.superBreakerCooldownMap.remove(player);
        });
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (MinerHandler.INSTANCE.superBreakerMap.containsKey(player.getUniqueID()))
            MinerHandler.INSTANCE.superBreakerMap.remove(player.getUniqueID());
    }

    @SubscribeEvent
    public void onDrop(PlayerInteractEvent.LeftClickBlock event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        if (player.getHeldItemMainhand().getItem() != Items.WOODEN_PICKAXE &&
                player.getHeldItemMainhand().getItem() != Items.STONE_PICKAXE &&
                player.getHeldItemMainhand().getItem() != Items.IRON_PICKAXE &&
                player.getHeldItemMainhand().getItem() != Items.GOLDEN_PICKAXE &&
                player.getHeldItemMainhand().getItem() != Items.DIAMOND_PICKAXE) {
            return;
        }

        if (!MinerHandler.INSTANCE.isProfession(player)) return;

        if(MinerHandler.INSTANCE.superBreakerMap.containsKey(player.getUniqueID())) {
            BlockEvent.BreakEvent e = new BlockEvent.BreakEvent(event.getWorld(), event.getPos(), event.getWorld().getBlockState(event.getPos()), event.getEntityPlayer());
            MinecraftForge.EVENT_BUS.post(e);
            if(!e.isCanceled()) {
                player.getEntityWorld().setBlockToAir(event.getPos());
                player.getHeldItemMainhand().setItemDamage(player.getHeldItemMainhand().getItemDamage() + 1);
                if(player.getHeldItemMainhand().getItemDamage() >= player.getHeldItemMainhand().getMaxDamage())
                    player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                player.inventoryContainer.detectAndSendChanges();
            }
        }
    }
}
