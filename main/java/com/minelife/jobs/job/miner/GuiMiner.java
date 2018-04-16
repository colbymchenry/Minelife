package com.minelife.jobs.job.miner;

import com.minelife.jobs.EnumJob;
import com.minelife.jobs.job.GuiJobBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

public class GuiMiner extends GuiJobBase {

    public GuiMiner() {
        super(EnumJob.MINER);
    }

    @Override
    public String farewellMessage(EntityPlayer player) {
        return TextFormatting.GOLD + "Thank you, " + TextFormatting.RED + player.getName() + TextFormatting.GOLD + "! This will help build the city!";
    }
}
