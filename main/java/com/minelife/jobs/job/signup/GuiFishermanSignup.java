package com.minelife.jobs.job.signup;

import com.minelife.jobs.EnumJob;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedList;

public class GuiFishermanSignup extends GuiSignUp {

    public GuiFishermanSignup() {
        super(EnumJob.FISHERMAN);
    }

    @Override
    public void addParts(LinkedList<String> parts) {
        parts.add("Hello, " + mc.player.getName() + "!");
        parts.add("Wanting to sit by the calm waters and catch some fish eh?");
        parts.add("Fishing is a great, easy, and relaxing way to make money.");
        parts.add("There are " + TextFormatting.UNDERLINE + "over 32 different" + TextFormatting.RESET + " types of fish to catch!");
        parts.add("You can turn in almost any of the fish you catch to me and I will give them to the city.");
        parts.add("Do you want to be a fisherman?");
    }
}