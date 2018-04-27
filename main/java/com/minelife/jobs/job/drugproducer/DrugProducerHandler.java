package com.minelife.jobs.job.drugproducer;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.drugs.ModDrugs;
import com.minelife.essentials.ModEssentials;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.ModJobs;
import com.minelife.jobs.NPCHandler;
import com.minelife.jobs.job.SellingOption;
import com.minelife.jobs.network.PacketOpenNormalGui;
import com.minelife.jobs.network.PacketOpenSignupGui;
import com.minelife.jobs.server.CommandJob;
import com.minelife.util.PacketPlaySound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.sql.SQLException;
import java.util.List;

public class DrugProducerHandler extends NPCHandler {

    public static final DrugProducerHandler INSTANCE = new DrugProducerHandler();

    private DrugProducerHandler() {
        super("drugproducer");
    }

    @Override
    public void onEntityRightClick(EntityPlayer player) {
        if(player.world.isRemote) return;

        if(!isProfession((EntityPlayerMP) player)) {
            Minelife.getNetwork().sendTo(new PacketOpenSignupGui(EnumJob.DRUG_PRODUCER), (EntityPlayerMP) player);
        } else {
            Minelife.getNetwork().sendTo(new PacketOpenNormalGui(EnumJob.DRUG_PRODUCER), (EntityPlayerMP) player);
        }
    }

    @Override
    public void joinProfession(EntityPlayer player) {
        if(isProfession((EntityPlayerMP) player)) {
            CommandJob.sendMessage(player, EnumJob.DRUG_PRODUCER, TextFormatting.RED + "You are already a drug producer.");
            return;
        }

        try {
            ModJobs.getDatabase().query("INSERT INTO drugproducer (playerID, xp) VALUES ('" + player.getUniqueID().toString() + "', '0')");
            Minelife.getNetwork().sendTo(new PacketPlaySound("minelife:gang_created", 0.1F, 1F), (EntityPlayerMP) player);
            ModEssentials.sendTitle(TextFormatting.RED.toString() + TextFormatting.BOLD.toString() + "Drug Producer", TextFormatting.GOLD.toString() + "Farm! Farm! Farm! FARM!!!", 12, (EntityPlayerMP) player);
        } catch (SQLException e) {
            e.printStackTrace();
            CommandJob.sendMessage(player, EnumJob.DRUG_PRODUCER, TextFormatting.RED + "Something went wrong. Notify an admin.");
        }
    }

    @Override
    public List<SellingOption> getSellingOptions() {
        return Lists.newArrayList(new SellingOption(new ItemStack(ModDrugs.itemJoint), 2048),new SellingOption(new ItemStack(ModDrugs.itemHempBuds), 2000));
    }

    @Override
    public void setupConfig() {

    }

    public boolean doDoubleJoint(EntityPlayerMP player) {
        double chance = (double) getLevel(player) / (double) getConfig().getInt("MaxLevel");
        return MathHelper.nextDouble(player.world.rand, 0, 100) < chance * 100.0D;
    }
}
