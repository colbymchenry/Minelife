package com.minelife.jobs.job.miner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.ModJobs;
import com.minelife.jobs.NPCHandler;
import com.minelife.jobs.job.SellingOption;
import com.minelife.jobs.job.fisherman.FishermanHandler;
import com.minelife.jobs.network.PacketOpenNormalGui;
import com.minelife.jobs.network.PacketOpenSignupGui;
import com.minelife.jobs.server.CommandJob;
import com.minelife.util.ItemHelper;
import com.minelife.util.NumberConversions;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import ic2.core.ref.BlockName;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentOxygen;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MinerHandler extends NPCHandler {

    public static final MinerHandler INSTANCE = new MinerHandler();

    private MinerHandler() {
        super("miner");
    }

    @Override
    public void onEntityRightClick(EntityPlayer player) {
        if (player.world.isRemote) return;

        if (!isProfession((EntityPlayerMP) player)) {
            Minelife.getNetwork().sendTo(new PacketOpenSignupGui(EnumJob.MINER), (EntityPlayerMP) player);
        } else {
            Minelife.getNetwork().sendTo(new PacketOpenNormalGui(EnumJob.MINER), (EntityPlayerMP) player);
        }
    }

    @Override
    public void joinProfession(EntityPlayer player) {
        if (isProfession((EntityPlayerMP) player)) {
            CommandJob.sendMessage(player, EnumJob.MINER, TextFormatting.RED + "You are already a miner.");
            return;
        }

        try {
            ModJobs.getDatabase().query("INSERT INTO miner (playerID, xp) VALUES ('" + player.getUniqueID().toString() + "', '0')");

            ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                    new int[]{Color.AQUA.asRGB(), Color.ORANGE.asRGB()}, new int[]{Color.GRAY.asRGB(), Color.SILVER.asRGB()}).getStack(1);

            EntityFireworkRocket ent = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent);
            EntityFireworkRocket ent1 = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent1);
        } catch (SQLException e) {
            e.printStackTrace();
            CommandJob.sendMessage(player, EnumJob.MINER, TextFormatting.RED + "Something went wrong. Notify an admin.");
        }
    }

    @Override
    public List<SellingOption> getSellingOptions() {
        List<SellingOption> sellingOptions = Lists.newArrayList();
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.GOLD_ORE), 1, 0), 2048));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(Blocks.IRON_ORE), 1, 0), 256));
        sellingOptions.add(new SellingOption(new ItemStack(Items.DIAMOND, 1, 0), 8192));
        sellingOptions.add(new SellingOption(new ItemStack(Items.COAL, 1, 0), 128));
        sellingOptions.add(new SellingOption(new ItemStack(Items.DYE, 1, 4), 864));
        sellingOptions.add(new SellingOption(new ItemStack(Items.REDSTONE, 1, 0), 64));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(BlockName.resource.getInstance()), 1, 1), 128));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(BlockName.resource.getInstance()), 1, 2), 512));
        sellingOptions.add(new SellingOption(new ItemStack(Item.getItemFromBlock(BlockName.resource.getInstance()), 1, 3), 256));
        return sellingOptions;
    }

    @Override
    public void setupConfig() {
        getConfig().addDefault("MaxLevel", 1500);
        getConfig().addDefault("ores", Lists.newArrayList("minecraft:coal_ore;50", "minecraft:diamond_ore;50"));
        getConfig().save();
    }

    public int getXPForBlock(Block block) {
        for (String crops : getConfig().getStringList("ores")) {
            if (crops.contains(";")) {
                if (NumberConversions.isInt(crops.split("\\;")[1])) {
                    if (crops.split("\\;")[0].equalsIgnoreCase(block.getRegistryName().toString())) {
                        return NumberConversions.toInt(crops.split("\\;")[1]);
                    }
                }
            }
        }
        return 0;
    }

    @SideOnly(Side.SERVER)
    public void applySuperBreaker(EntityPlayerMP player) {
        int duration = 2 + (getLevel(player) / 50);

        if (player.getHeldItemMainhand().getItem() != Items.WOODEN_PICKAXE &&
                player.getHeldItemMainhand().getItem() != Items.STONE_PICKAXE &&
                player.getHeldItemMainhand().getItem() != Items.IRON_PICKAXE &&
                player.getHeldItemMainhand().getItem() != Items.GOLDEN_PICKAXE &&
                player.getHeldItemMainhand().getItem() != Items.DIAMOND_PICKAXE) {
            return;
        }

        if(superBreakerCooldownMap.containsKey(player.getUniqueID())) {
            CommandJob.sendMessage(player, EnumJob.MINER, "You must wait " + TextFormatting.RED + ((superBreakerCooldownMap.get(player.getUniqueID()) - System.currentTimeMillis()) / 1000) + TextFormatting.GOLD + " seconds before reactivating Super Breaker.");
            return;
        }

        superBreakerMap.put(player.getUniqueID(), System.currentTimeMillis() + (duration * 1000L));
        superBreakerCooldownMap.put(player.getUniqueID(), System.currentTimeMillis() + (240 * 1000L));
        CommandJob.sendMessage(player, EnumJob.MINER, "Super Breaker activated for " + TextFormatting.RED + duration + TextFormatting.GOLD + " seconds!");
        Minelife.getNetwork().sendTo(new PacketPlaySound("minecraft:entity.player.levelup", 1, 1), player);
    }

    public boolean doDoubleDrop(EntityPlayerMP player) {
        double chance = getLevel(player) / getConfig().getInt("MaxLevel");
        return MathHelper.nextDouble(player.world.rand, 0, 100) < chance * 100.0D;
    }

    public Map<UUID, Long> superBreakerMap = Maps.newHashMap();
    public Map<UUID, Long> superBreakerCooldownMap = Maps.newHashMap();

}
