package com.minelife.jobs.job.fisherman;

import com.minelife.jobs.EnumJob;
import com.minelife.jobs.job.GuiJobBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

public class GuiFisherman extends GuiJobBase {

    public GuiFisherman() {
        super(EnumJob.FISHERMAN);
    }

    @Override
    public String farewellMessage(EntityPlayer player) {
        return TextFormatting.GOLD + "Thank you, " + TextFormatting.RED + player.getName() + TextFormatting.GOLD + "! This will help feed the city!";
    }
}
