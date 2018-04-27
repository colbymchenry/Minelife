package com.minelife.jobs.job.drugproducer;

import com.minelife.jobs.EnumJob;
import com.minelife.jobs.job.GuiJobBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

public class GuiDrugProducer extends GuiJobBase {

    public GuiDrugProducer() {
        super(EnumJob.DRUG_PRODUCER);
    }

    @Override
    public String farewellMessage(EntityPlayer player) {
        return TextFormatting.GOLD + "Thanks cuz! I'll distribute this out immediately.";
    }
}
