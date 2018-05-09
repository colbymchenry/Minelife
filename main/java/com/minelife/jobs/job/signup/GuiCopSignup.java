package com.minelife.jobs.job.signup;

import com.minelife.jobs.EnumJob;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedList;

public class GuiCopSignup extends GuiSignUp {

    public GuiCopSignup() {
        super(EnumJob.COP);
    }

    @Override
    public void addParts(LinkedList<String> parts) {
        parts.add("Freeze, " + mc.player.getName() + "!");
        parts.add(TextFormatting.DARK_GRAY + "Oh... sorry... old habits die hard son.");
        parts.add(TextFormatting.DARK_GRAY + "Are you wanting to join the force?");
        parts.add(TextFormatting.DARK_GRAY + "We could really use people like you.");
        parts.add(TextFormatting.DARK_GRAY + "The city has gone rampant with thieves, drug dealers, and murderers.");
        parts.add(TextFormatting.DARK_GRAY + "The city needs people like us, the brave ones, to combat those horrific people.");
        parts.add(TextFormatting.DARK_GRAY + "What do you say? Want to become an officer?");
    }
}
