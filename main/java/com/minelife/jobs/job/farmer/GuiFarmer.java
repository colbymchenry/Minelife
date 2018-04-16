package com.minelife.jobs.job.farmer;

import com.minelife.jobs.EnumJob;
import com.minelife.jobs.job.GuiJobBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

public class GuiFarmer extends GuiJobBase {

    public GuiFarmer() {
        super(EnumJob.FARMER);
    }

    @Override
    public String farewellMessage(EntityPlayer player) {
        return TextFormatting.GOLD + "Thank you, " + TextFormatting.RED + player.getName() + TextFormatting.GOLD + "! This will help feed the city!";
    }
}
