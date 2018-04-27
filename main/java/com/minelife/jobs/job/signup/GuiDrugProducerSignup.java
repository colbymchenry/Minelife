package com.minelife.jobs.job.signup;

import com.minelife.jobs.EnumJob;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedList;

public class GuiDrugProducerSignup extends GuiSignUp {

    public GuiDrugProducerSignup() {
        super(EnumJob.DRUG_PRODUCER);
    }

    @Override
    public void addParts(LinkedList<String> parts) {
        parts.add("Hello, " + mc.player.getName() + "!");
        parts.add(TextFormatting.DARK_RED + "You think you've got what it takes to be a Drug Producer?");
        parts.add(TextFormatting.DARK_RED + "Growing marijuana isn't an easy task and requires a decent setup.");
        parts.add(TextFormatting.DARK_RED + "If you are caught by the police selling or growing marijuana you are");
        parts.add(TextFormatting.DARK_RED + "jailed for 20 minutes to a maximum of an hour!");
        parts.add(TextFormatting.DARK_RED + "However, drug producers make MAD $$$! With risk comes reward.");
        parts.add(TextFormatting.DARK_RED + "Do you want to be a Drug Producer?");
    }
}
