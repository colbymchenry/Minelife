package com.minelife.jobs.job.signup;

import com.minelife.jobs.EnumJob;

import java.util.LinkedList;

public class GuiMinerSignup extends GuiSignUp {

    public GuiMinerSignup() {
        super(EnumJob.MINER);
    }

    @Override
    public void addParts(LinkedList<String> parts) {
        parts.add("Hello, " + mc.player.getName() + "!");
        parts.add("Want to gather ores and gems for the city?");
        parts.add("Being a miner is dirty work.");
        parts.add("Luckily the mining shaft regenerates every 30 minutes so we can keep mining!");
        parts.add("You can turn in any ores or gems you find to me for cash and I will give them to the city!");
        parts.add("Do you want to be a miner?");
    }
}
