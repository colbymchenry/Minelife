package com.minelife.jobs.job.signup;

import com.minelife.jobs.EnumJob;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedList;

public class GuiEMTSignup extends GuiSignUp {

    public GuiEMTSignup() {
        super(EnumJob.BOUNTY_HUNTER);
    }

    @Override
    public void addParts(LinkedList<String> parts) {
        parts.add("Hello, " + mc.player.getName() + "!");
        parts.add(TextFormatting.DARK_GRAY + "You think you've got what it takes to be an EMT?");
        parts.add(TextFormatting.DARK_GRAY + "Not many can handle the horrific sights that are involved with being an EMT.");
        parts.add(TextFormatting.DARK_GRAY + "You will have an extra $1024 with your welfare at the end of the day.");
        parts.add(TextFormatting.DARK_GRAY + "The people will rely on you to bring them back to life. You are VERY important.");
        parts.add(TextFormatting.DARK_RED + "If you don't want to be an EMT anymore come back and talk to me.");
        parts.add(TextFormatting.DARK_GRAY + "Are you sure you can handle being an EMT?");
    }
}
