package com.minelife.jobs.job.farmer;

import com.minelife.Minelife;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.NPCHandler;
import com.minelife.jobs.network.PacketOpenSignupGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

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

    public boolean replant(EntityPlayerMP player) {
        double chance = getLevel(player) / config.getInt("MaxLevel");
        return r.nextInt(100) < chance;
    }

    public int getGrowthStage(EntityPlayerMP player) {
        return 5 - (config.getInt("MaxLevel") / getLevel(player));
    }
}