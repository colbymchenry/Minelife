package com.minelife.jobs.job.fisherman;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.ModJobs;
import com.minelife.jobs.NPCHandler;
import com.minelife.jobs.job.SellingOption;
import com.minelife.jobs.network.PacketOpenNormalGui;
import com.minelife.jobs.network.PacketOpenSignupGui;
import com.minelife.jobs.server.CommandJob;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import com.pam.harvestcraft.item.ItemRegistry;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.sql.SQLException;
import java.util.List;

public class FishermanHandler extends NPCHandler {

    public static final FishermanHandler INSTANCE = new FishermanHandler();

    private FishermanHandler() {
        super("fisherman");
    }

    @Override
    public void onEntityRightClick(EntityPlayer player) {
        if(player.world.isRemote) return;

        if(!isProfession((EntityPlayerMP) player)) {
            Minelife.getNetwork().sendTo(new PacketOpenSignupGui(EnumJob.FISHERMAN), (EntityPlayerMP) player);
        } else {
            Minelife.getNetwork().sendTo(new PacketOpenNormalGui(EnumJob.FISHERMAN), (EntityPlayerMP) player);
        }
    }

    @Override
    public void joinProfession(EntityPlayer player) {
        if(isProfession((EntityPlayerMP) player)) {
            CommandJob.sendMessage(player, EnumJob.FISHERMAN, TextFormatting.RED + "You are already a fisherman.");
            return;
        }

        try {
            ModJobs.getDatabase().query("INSERT INTO fisherman (playerID, xp) VALUES ('" + player.getUniqueID().toString() + "', '0')");

            ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                    new int[]{Color.WHITE.asRGB(), Color.BLUE.asRGB()}, new int[]{Color.NAVY.asRGB(), Color.AQUA.asRGB()}).getStack(1);

            EntityFireworkRocket ent = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent);
            EntityFireworkRocket ent1 = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent1);
        } catch (SQLException e) {
            e.printStackTrace();
            CommandJob.sendMessage(player, EnumJob.FISHERMAN, TextFormatting.RED + "Something went wrong. Notify an admin.");
        }
    }

    @Override
    public List<SellingOption> getSellingOptions() {
        List<SellingOption> sellingOptions = Lists.newArrayList();
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.anchovyrawItem), 24));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.bassrawItem), 96));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.carprawItem), 42));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.catfishrawItem), 46));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.eelrawItem), 96));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.frograwItem), 24));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.grouperrawItem), 64));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.herringrawItem), 64));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.jellyfishrawItem), 72));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.mudfishrawItem), 64));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.octopusrawItem), 256));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.perchrawItem), 256));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.scalloprawItem), 24));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.shrimprawItem), 12));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.snailrawItem), 12));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.snapperrawItem), 72));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.tilapiarawItem), 120));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.troutrawItem), 98));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.tunarawItem), 64));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.turtlerawItem), 52));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.walleyerawItem), 24));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.greenheartfishItem), 86));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.sardinerawItem), 84));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.musselrawItem), 72));
        sellingOptions.add(new SellingOption(new ItemStack(ItemRegistry.rawtofishItem), 64));
        return sellingOptions;
    }

    @Override
    public void setupConfig() {

    }
}
