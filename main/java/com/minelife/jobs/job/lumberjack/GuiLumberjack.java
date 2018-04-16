package com.minelife.jobs.job.lumberjack;

import com.minelife.jobs.EnumJob;
import com.minelife.jobs.job.GuiJobBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

public class GuiLumberjack extends GuiJobBase {

    public GuiLumberjack() {
        super(EnumJob.LUMBERJACK);
    }

    @Override
    public String farewellMessage(EntityPlayer player) {
        return TextFormatting.GOLD + "Thank you, " + TextFormatting.RED + player.getName() + TextFormatting.GOLD + "! This will help build the city!";
    }
}
