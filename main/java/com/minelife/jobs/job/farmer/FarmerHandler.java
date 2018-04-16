package com.minelife.jobs.job.farmer;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.ModJobs;
import com.minelife.jobs.NPCHandler;
import com.minelife.jobs.job.SellingOption;
import com.minelife.jobs.network.PacketOpenNormalGui;
import com.minelife.jobs.network.PacketOpenSignupGui;
import com.minelife.jobs.server.CommandJob;
import com.minelife.util.NumberConversions;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import com.pam.harvestcraft.blocks.BlockRegistry;
import com.pam.harvestcraft.blocks.CropRegistry;
import com.pam.harvestcraft.blocks.FruitRegistry;
import com.pam.harvestcraft.item.ItemRegistry;
import com.pam.harvestcraft.item.items.ItemPamSeedFood;
import com.sun.scenario.effect.Crop;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.sql.SQLException;
import java.util.List;

public class FarmerHandler extends NPCHandler {

    public static final FarmerHandler INSTANCE = new FarmerHandler();

    private FarmerHandler() {
        super("farmer");
    }

    @Override
    public void onEntityRightClick(EntityPlayer player) {
        if (player.world.isRemote) return;

        if (!isProfession((EntityPlayerMP) player)) {
            Minelife.getNetwork().sendTo(new PacketOpenSignupGui(EnumJob.FARMER), (EntityPlayerMP) player);
        } else {
            Minelife.getNetwork().sendTo(new PacketOpenNormalGui(EnumJob.FARMER), (EntityPlayerMP) player);
        }
    }

    @Override
    public void joinProfession(EntityPlayer player) {
        if (isProfession((EntityPlayerMP) player)) {
            CommandJob.sendMessage(player, EnumJob.FARMER, TextFormatting.RED + "You are already a farmer.");
            return;
        }

        try {
            ModJobs.getDatabase().query("INSERT INTO farmer (playerID, xp) VALUES ('" + player.getUniqueID().toString() + "', '0')");

            ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                    new int[]{Color.YELLOW.asRGB(), Color.LIME.asRGB()}, new int[]{Color.YELLOW.asRGB(), Color.GREEN.asRGB()}).getStack(1);

            EntityFireworkRocket ent = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent);
            EntityFireworkRocket ent1 = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent1);
        } catch (SQLException e) {
            e.printStackTrace();
            CommandJob.sendMessage(player, EnumJob.FARMER, TextFormatting.RED + "Something went wrong. Notify an admin.");
        }
    }

    @Override
    public List<SellingOption> getSellingOptions() {
        List<SellingOption> sellingOptions = Lists.newArrayList();
        sellingOptions.add(new SellingOption(new ItemStack(Items.WHEAT, 16), 384));
        sellingOptions.add(new SellingOption(new ItemStack(Items.WHEAT_SEEDS, 16), 256));
        sellingOptions.add(new SellingOption(new ItemStack(Items.REEDS, 4), 128));
        sellingOptions.add(new SellingOption(new ItemStack(Items.MELON, 5), 80));
        sellingOptions.add(new SellingOption(new ItemStack(Items.MELON_SEEDS, 5), 80));
        sellingOptions.add(new SellingOption(new ItemStack(Items.POTATO, 4), 256));
        sellingOptions.add(new SellingOption(new ItemStack(Items.CARROT, 4), 256));

        CropRegistry.getCrops().forEach((cropName, blockCrop) -> {
            sellingOptions.add(new SellingOption(new ItemStack(Item.getByNameOrId("harvestcraft:" + cropName.toLowerCase() + "item")), 64));
        });

        FruitRegistry.foodItems.forEach((name, item) -> {
            sellingOptions.add(new SellingOption(new ItemStack(item), 64));
        });

        return sellingOptions;
    }

    @Override
    public void setupConfig() {

    }

    public boolean replant(EntityPlayerMP player) {
        double chance = getLevel(player) / config.getInt("MaxLevel");
        return r.nextInt(100) < chance;
    }

    public int getGrowthStage(EntityPlayerMP player) {
        return 5 - (config.getInt("MaxLevel") / getLevel(player));
    }

    public int getXPForBlock(Block block) {
        for (String crops : getConfig().getStringList("crops")) {
            if (crops.contains(";")) {
                if (NumberConversions.isInt(crops.split("\\;")[1])) {
                    if (crops.split("\\;")[0].equalsIgnoreCase(block.getUnlocalizedName().replaceFirst("tile.", ""))) {
                        return NumberConversions.toInt(crops.split("\\;")[1]);
                    }
                }
            }
        }
        return 0;
    }


}