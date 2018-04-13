package com.minelife.jobs.job.signup;

import com.minelife.jobs.EnumJob;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedList;

public class GuiBountyHunterSignup extends GuiSignUp {

    public GuiBountyHunterSignup() {
        super(EnumJob.BOUNTY_HUNTER);
    }

    @Override
    public void addParts(LinkedList<String> parts) {
        parts.add("Hello, " + mc.player.getName() + "!");
        parts.add(TextFormatting.DARK_RED + "You think you've got what it takes to be a Bounty Hunter?");
        parts.add(TextFormatting.DARK_RED + "Taking down some of the toughest people in the land is no easy task.");
        parts.add(TextFormatting.DARK_RED + "However, Bounty Hunters make some of the best money in the land as well.");
        parts.add(TextFormatting.DARK_RED + "You will have to risk your life for money, which some can't swallow that pill.");
        parts.add(TextFormatting.DARK_RED + "Are you sure you want to be a Bounty Hunter?");
    }
}
