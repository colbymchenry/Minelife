package com.minelife.jobs.job.farmer;

import com.minelife.Minelife;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.ModJobs;
import com.minelife.jobs.NPCHandler;
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

public class FarmerHandler extends NPCHandler  {

    public static FarmerHandler INSTANCE = new FarmerHandler();

    private FarmerHandler() {
        super("farmer");
    }

    @Override
    public void onEntityRightClick(EntityPlayer player) {
        if(player.world.isRemote) return;

        if(!isProfession((EntityPlayerMP) player)) {
            Minelife.getNetwork().sendTo(new PacketOpenSignupGui(EnumJob.FARMER), (EntityPlayerMP) player);
        }
    }

    @Override
    public void joinProfession(EntityPlayer player) {
        if(isProfession((EntityPlayerMP) player)) {
            CommandJob.sendMessage(player, EnumJob.FARMER, TextFormatting.RED + "You are already a farmer.");
            return;
        }

        try {
            ModJobs.getDatabase().query("INSERT INTO farmer (playerID, xp) VALUES ('" + player.getUniqueID().toString() + "', '0.0')");

            ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                    new int[]{Color.WHITE.asRGB(), Color.LIME.asRGB()}, new int[]{Color.YELLOW.asRGB(), Color.GREEN.asRGB()}).getStack(1);

            EntityFireworkRocket ent = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent);
            EntityFireworkRocket ent1 = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent1);
        } catch (SQLException e) {
            e.printStackTrace();
            CommandJob.sendMessage(player, EnumJob.FARMER, TextFormatting.RED + "Something went wrong. Notify an admin.");
        }
    }

    public boolean replant(EntityPlayerMP player) {
        double chance = getLevel(player) / config.getInt("MaxLevel");
        return r.nextInt(100) < chance;
    }

    public int getGrowthStage(EntityPlayerMP player) {
        return 5 - (config.getInt("MaxLevel") / getLevel(player));
    }
}