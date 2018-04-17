package com.minelife.jobs.job.lumberjack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.ModJobs;
import com.minelife.jobs.NPCHandler;
import com.minelife.jobs.job.SellingOption;
import com.minelife.jobs.job.bountyhunter.BountyHunterHandler;
import com.minelife.jobs.job.miner.MinerHandler;
import com.minelife.jobs.network.PacketOpenNormalGui;
import com.minelife.jobs.network.PacketOpenSignupGui;
import com.minelife.jobs.server.CommandJob;
import com.minelife.util.NumberConversions;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import com.pam.harvestcraft.blocks.FruitRegistry;
import com.pam.harvestcraft.blocks.growables.BlockPamSapling;
import com.pam.harvestcraft.item.ItemRegistry;
import ic2.core.ref.BlockName;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LumberjackHandler extends NPCHandler {

    public static final LumberjackHandler INSTANCE = new LumberjackHandler();

    private LumberjackHandler() {
        super("lumberjack");
    }

    @Override
    public void onEntityRightClick(EntityPlayer player) {
        if (player.world.isRemote) return;

        if (!isProfession((EntityPlayerMP) player)) {
            Minelife.getNetwork().sendTo(new PacketOpenSignupGui(EnumJob.LUMBERJACK), (EntityPlayerMP) player);
        } else {
            Minelife.getNetwork().sendTo(new PacketOpenNormalGui(EnumJob.LUMBERJACK), (EntityPlayerMP) player);
        }
    }

    @Override
    public void joinProfession(EntityPlayer player) {
        if (isProfession((EntityPlayerMP) player)) {
            CommandJob.sendMessage(player, EnumJob.LUMBERJACK, TextFormatting.RED + "You are already a lumberjack.");
            return;
        }

        try {
            ModJobs.getDatabase().query("INSERT INTO lumberjack (playerID, xp) VALUES ('" + player.getUniqueID().toString() + "', '0')");

            ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                    new int[]{Color.GREEN.asRGB(), Color.ORANGE.asRGB()}, new int[]{Color.OLIVE.asRGB(), Color.WHITE.asRGB()}).getStack(1);

            EntityFireworkRocket ent = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent);
            EntityFireworkRocket ent1 = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent1);
        } catch (SQLException e) {
            e.printStackTrace();
            CommandJob.sendMessage(player, EnumJob.LUMBERJACK, TextFormatting.RED + "Something went wrong. Notify an admin.");
        }
    }

    @Override
    public List<SellingOption> getSellingOptions() {
        List<SellingOption> sellingOptions = Lists.newArrayList();
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.LOG), 1, 0), 32));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.LOG), 1, 1), 32));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.LOG), 1, 2), 32));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.LOG), 1, 3), 32));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.LOG2), 1, 0), 32));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.LOG2), 1, 1), 32));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.LOG2), 1, 1), 32));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.LEAVES), 1, 0), 1));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.LEAVES), 1, 1), 1));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.LEAVES), 1, 2), 1));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.LEAVES), 1, 3), 1));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.LEAVES2), 1, 0), 1));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.LEAVES2), 1, 1), 1));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.SAPLING), 1, 0), 32));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.SAPLING), 1, 1), 32));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.SAPLING), 1, 2), 32));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.SAPLING), 1, 3), 32));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.SAPLING), 1, 4), 32));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.SAPLING), 1, 5), 32));
        for (BlockPamSapling blockPamSapling : FruitRegistry.getSaplings())
            sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(blockPamSapling), 1), 32));
        return sellingOptions;
    }

    @Override
    public void setupConfig() {
        getConfig().addDefault("MaxLevel", 1500);
        getConfig().save();
    }

    public int getXPForBlock(Block block, int meta) {
        if (block == Blocks.LOG) {
            if (meta == 0) return 70;
            if (meta == 1) return 80;
            if (meta == 3) return 100;
            return 90;
        }
        return block == Blocks.LOG2 ? 90 : 0;
    }

    @SideOnly(Side.SERVER)
    public void applyTreeFeller(EntityPlayerMP player) {
        int duration = 2 + (getLevel(player) / 50);

        if (player.getHeldItemMainhand().getItem() != Items.WOODEN_AXE &&
                player.getHeldItemMainhand().getItem() != Items.STONE_AXE &&
                player.getHeldItemMainhand().getItem() != Items.IRON_AXE &&
                player.getHeldItemMainhand().getItem() != Items.GOLDEN_AXE &&
                player.getHeldItemMainhand().getItem() != Items.DIAMOND_AXE) {
            return;
        }

        if (treeFellerCooldownMap.containsKey(player.getUniqueID())) {
            CommandJob.sendMessage(player, EnumJob.MINER, "You must wait " + TextFormatting.RED + ((treeFellerCooldownMap.get(player.getUniqueID()) - System.currentTimeMillis()) / 1000) + TextFormatting.GOLD + " seconds before reactivating Tree Feller.");
            return;
        }

        treeFellerMap.put(player.getUniqueID(), System.currentTimeMillis() + (duration * 1000L));
        treeFellerCooldownMap.put(player.getUniqueID(), System.currentTimeMillis() + (240 * 1000L));
        CommandJob.sendMessage(player, EnumJob.MINER, "Tree Feller activated for " + TextFormatting.RED + duration + TextFormatting.GOLD + " seconds!");
        Minelife.getNetwork().sendTo(new PacketPlaySound("minecraft:entity.player.levelup", 1, 1), player);
    }

    public boolean doDoubleDrop(EntityPlayerMP player) {
        double chance = getLevel(player) / getConfig().getInt("MaxLevel");
        return MathHelper.nextDouble(player.world.rand, 0, 100) < chance * 100.0D;
    }

    public Map<UUID, Long> treeFellerMap = Maps.newHashMap();
    public Map<UUID, Long> treeFellerCooldownMap = Maps.newHashMap();

}