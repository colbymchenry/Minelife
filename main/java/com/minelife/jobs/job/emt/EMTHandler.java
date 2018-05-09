package com.minelife.jobs.job.emt;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.emt.ModEMT;
import com.minelife.essentials.ModEssentials;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.ModJobs;
import com.minelife.jobs.NPCHandler;
import com.minelife.jobs.job.SellingOption;
import com.minelife.jobs.network.PacketOpenNormalGui;
import com.minelife.jobs.network.PacketOpenSignupGui;
import com.minelife.jobs.server.CommandJob;
import com.minelife.police.ModPolice;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.sql.SQLException;
import java.util.List;

public class EMTHandler extends NPCHandler {

    public static final EMTHandler INSTANCE = new EMTHandler();

    private EMTHandler() {
        super("emt");
    }

    @Override
    public void onEntityRightClick(EntityPlayer player) {
        if (player.world.isRemote) return;

        if (!ModEMT.isEMT(player.getUniqueID())) {
            Minelife.getNetwork().sendTo(new PacketOpenSignupGui(EnumJob.EMT), (EntityPlayerMP) player);
        } else {
            ModEMT.setEMT(player, false);
            player.sendMessage(new TextComponentString("You are no longer an EMT!"));
        }
    }

    @Override
    public void joinProfession(EntityPlayer player) {
        if (ModEMT.isEMT(player.getUniqueID())) {
            CommandJob.sendMessage(player, EnumJob.EMT, TextFormatting.RED + "You are already an EMT.");
            return;
        }

        if(ModPolice.isCop(player.getUniqueID())) {
            CommandJob.sendMessage(player, EnumJob.EMT, TextFormatting.RED + "You are already a cop.");
            return;
        }

        ModEMT.setEMT(player, true);

        ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                new int[]{Color.BLUE.asRGB(), Color.ORANGE.asRGB()}, new int[]{Color.WHITE.asRGB(), Color.AQUA.asRGB()}).getStack(1);

        EntityFireworkRocket ent = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
        player.getEntityWorld().spawnEntity(ent);
        EntityFireworkRocket ent1 = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
        player.getEntityWorld().spawnEntity(ent1);

        ModEssentials.sendTitle(null ,TextFormatting.RED + "Right click an unconscious player to revive them!", 6, (EntityPlayerMP) player);
    }

    @Override
    public List<SellingOption> getSellingOptions() {
        return Lists.newArrayList();
    }

    @Override
    public void setupConfig() {

    }

}
