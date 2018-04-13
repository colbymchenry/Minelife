package com.minelife.jobs.job.lumberjack;

import com.minelife.Minelife;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.ModJobs;
import com.minelife.jobs.NPCHandler;
import com.minelife.jobs.job.bountyhunter.BountyHunterHandler;
import com.minelife.jobs.network.PacketOpenSignupGui;
import com.minelife.jobs.server.CommandJob;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.sql.SQLException;

public class LumberjackHandler extends NPCHandler {

    public static final LumberjackHandler INSTANCE = new LumberjackHandler();

    private LumberjackHandler() {
        super("lumberjack");
    }

    @Override
    public void onEntityRightClick(EntityPlayer player) {
        if(player.world.isRemote) return;

        if(!isProfession((EntityPlayerMP) player)) {
            Minelife.getNetwork().sendTo(new PacketOpenSignupGui(EnumJob.LUMBERJACK), (EntityPlayerMP) player);
        }
    }

    @Override
    public void joinProfession(EntityPlayer player) {
        if(isProfession((EntityPlayerMP) player)) {
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
}