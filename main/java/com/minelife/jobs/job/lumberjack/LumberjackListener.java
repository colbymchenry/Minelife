package com.minelife.jobs.job.lumberjack;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.essentials.ModEssentials;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.job.miner.MinerHandler;
import com.minelife.jobs.server.CommandJob;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.PlayerHelper;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LumberjackListener {

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();

        if (player == null) return;

        if (!LumberjackHandler.INSTANCE.isProfession(player)) return;

        if (event.isCanceled()) return;

        int level = LumberjackHandler.INSTANCE.getLevel(player);
        int xp = LumberjackHandler.INSTANCE.getXPForBlock(event.getWorld().getBlockState(event.getPos()).getBlock(),
                event.getWorld().getBlockState(event.getPos()).getBlock().getMetaFromState(event.getWorld().getBlockState(event.getPos())));

        if (xp < 1) return;

        LumberjackHandler.INSTANCE.addXP(player.getUniqueID(), xp);
        CommandJob.sendMessage(player, EnumJob.LUMBERJACK, "+" + xp);

        if (LumberjackHandler.INSTANCE.doDoubleDrop(player)) {
            Minelife.getNetwork().sendTo(new PacketPlaySound("minecraft:entity.player.levelup", 1, 1), player);
            NonNullList<ItemStack> drops = NonNullList.create();
            event.getState().getBlock().getDrops(drops, event.getWorld(), event.getPos(), event.getState(),
                    EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItemMainhand()));
            drops.forEach(stack -> {
                stack.setCount(stack.getCount() + 1);
                player.dropItem(stack, false);
            });
        }

        if (LumberjackHandler.INSTANCE.getLevel(player) > level) {
            Minelife.getNetwork().sendTo(new PacketPlaySound("minelife:level_up", 1, 1), player);

            ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                    new int[]{Color.RED.asRGB(), Color.BLUE.asRGB()}, new int[]{Color.PURPLE.asRGB(), Color.WHITE.asRGB()}).getStack(1);

            EntityFireworkRocket ent = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent);

            ModEssentials.sendTitle(TextFormatting.YELLOW.toString() + TextFormatting.BOLD.toString() + "Level Up!",
                    TextFormatting.YELLOW + "New Level: " + TextFormatting.BLUE + LumberjackHandler.INSTANCE.getLevel(player), 5, player);
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickItem event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        if (player.getHeldItemMainhand().getItem() != Items.WOODEN_AXE &&
                player.getHeldItemMainhand().getItem() != Items.STONE_AXE &&
                player.getHeldItemMainhand().getItem() != Items.IRON_AXE &&
                player.getHeldItemMainhand().getItem() != Items.GOLDEN_AXE &&
                player.getHeldItemMainhand().getItem() != Items.DIAMOND_AXE) {
            return;
        }

        if (!LumberjackHandler.INSTANCE.isProfession(player)) return;

        LumberjackHandler.INSTANCE.applyTreeFeller(player);
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        if (player.getHeldItemMainhand().getItem() != Items.WOODEN_AXE &&
                player.getHeldItemMainhand().getItem() != Items.STONE_AXE &&
                player.getHeldItemMainhand().getItem() != Items.IRON_AXE &&
                player.getHeldItemMainhand().getItem() != Items.GOLDEN_AXE &&
                player.getHeldItemMainhand().getItem() != Items.DIAMOND_AXE) {
            return;
        }

        if (!LumberjackHandler.INSTANCE.isProfession(player)) return;

        LumberjackHandler.INSTANCE.applyTreeFeller(player);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        final List<UUID> toRemove = Lists.newArrayList();

        LumberjackHandler.INSTANCE.treeFellerMap.forEach((player, duration) -> {
            if (System.currentTimeMillis() > duration) toRemove.add(player);
        });
        toRemove.forEach(player -> {
            if (PlayerHelper.getPlayer(player) != null)
                CommandJob.sendMessage(PlayerHelper.getPlayer(player), EnumJob.LUMBERJACK, "Tree Feller deactivated.");
            LumberjackHandler.INSTANCE.treeFellerMap.remove(player);
        });

        toRemove.clear();
        LumberjackHandler.INSTANCE.treeFellerCooldownMap.forEach((player, duration) -> {
            if (System.currentTimeMillis() > duration) toRemove.add(player);
        });

        toRemove.forEach(player -> {
            if (PlayerHelper.getPlayer(player) != null) {
                Minelife.getNetwork().sendTo(new PacketPlaySound("minecraft:entity.player.levelup", 1, 1), PlayerHelper.getPlayer(player));
                CommandJob.sendMessage(PlayerHelper.getPlayer(player), EnumJob.LUMBERJACK, "Tree Feller is now available.");
            }
            LumberjackHandler.INSTANCE.treeFellerCooldownMap.remove(player);
        });
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (LumberjackHandler.INSTANCE.treeFellerMap.containsKey(player.getUniqueID()))
            LumberjackHandler.INSTANCE.treeFellerMap.remove(player.getUniqueID());
    }

    @SubscribeEvent
    public void onHit(PlayerInteractEvent.LeftClickBlock event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        World world = event.getWorld();
        IBlockState blockState = world.getBlockState(event.getPos());
        Block block = blockState.getBlock();

        if (player.getHeldItemMainhand().getItem() != Items.WOODEN_AXE &&
                player.getHeldItemMainhand().getItem() != Items.STONE_AXE &&
                player.getHeldItemMainhand().getItem() != Items.IRON_AXE &&
                player.getHeldItemMainhand().getItem() != Items.GOLDEN_AXE &&
                player.getHeldItemMainhand().getItem() != Items.DIAMOND_AXE) {
            return;
        }

        if (!LumberjackHandler.INSTANCE.isProfession(player)) return;

        /**
         * Do the leaf blower effect
         */
        if (block == Blocks.LEAVES || block == Blocks.LEAVES2) {
            if (LumberjackHandler.INSTANCE.getLevel(player) < 100) return;
            BlockEvent.BreakEvent e = new BlockEvent.BreakEvent(world, event.getPos(), blockState, player);
            MinecraftForge.EVENT_BUS.post(e);
            if (!e.isCanceled()) {
                Minelife.getNetwork().sendTo(new PacketPlaySound("minecraft:entity.chicken.egg", 1, 1), player);
                net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable) block;
                List<ItemStack> drops = target.onSheared(new ItemStack(Items.SHEARS), player.world, event.getPos(),
                        EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItemMainhand()));
                drops.forEach(stack -> player.dropItem(stack, false));
                NonNullList<ItemStack> defaultDrops = NonNullList.create();
                block.getDrops(defaultDrops, world, event.getPos(), blockState,
                        EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItemMainhand()));
                defaultDrops.forEach(stack -> player.dropItem(stack, false));

                world.setBlockToAir(event.getPos());
                player.getHeldItemMainhand().damageItem(1, player);
                if (player.getHeldItemMainhand().getItemDamage() >= player.getHeldItemMainhand().getMaxDamage())
                    player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                player.inventoryContainer.detectAndSendChanges();
            } else {
                return;
            }
            return;
        }

        /**
         * Do the Tree Feller effect
         */
        int xp = LumberjackHandler.INSTANCE.getXPForBlock(block, block.getMetaFromState(blockState));

        if (xp < 1) return;

        if (LumberjackHandler.INSTANCE.treeFellerMap.containsKey(player.getUniqueID())) {
            Set<BlockPos> logPositions = Sets.newTreeSet();
            gatherLogs(world, event.getPos(), logPositions);

            for (BlockPos logPosition : logPositions) {
                BlockEvent.BreakEvent e = new BlockEvent.BreakEvent(world, logPosition, world.getBlockState(logPosition), player);
                MinecraftForge.EVENT_BUS.post(e);
                if (!e.isCanceled()) {
                    NonNullList<ItemStack> drops = NonNullList.create();
                    world.getBlockState(logPosition).getBlock().getDrops(drops, world, event.getPos(), world.getBlockState(logPosition),
                            EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItemMainhand()));
                    drops.forEach(stack -> player.dropItem(stack, false));

                    world.setBlockToAir(logPosition);
                    player.getHeldItemMainhand().damageItem(1, player);
                    if (player.getHeldItemMainhand().getItemDamage() >= player.getHeldItemMainhand().getMaxDamage())
                        player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                    player.inventoryContainer.detectAndSendChanges();
                } else {
                    return;
                }
            }

        }
    }

    private void gatherLogs(World world, BlockPos pos, Set<BlockPos> logPositions) {
        for (BlockPos blockPos : getSurroundingLogs(world, pos)) {
            if (!logPositions.contains(blockPos) && logPositions.size() < 200) {
                logPositions.add(blockPos);
                gatherLogs(world, blockPos, logPositions);
            }
        }
    }

    private List<BlockPos> getSurroundingLogs(World world, BlockPos pos) {
        List<BlockPos> logs = Lists.newArrayList();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    if (isLog(world, pos.add(x, y, z))) logs.add(pos.add(x, y, z));
                }
            }
        }
        return logs;
    }

    private boolean isLog(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == Blocks.LOG || world.getBlockState(pos).getBlock() == Blocks.LOG2;
    }


}
