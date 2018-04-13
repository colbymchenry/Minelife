package com.minelife.jobs.job.signup;

import com.minelife.jobs.EnumJob;

import java.util.LinkedList;

public class GuiLumberjackSignup extends GuiSignUp {


    public GuiLumberjackSignup() {
        super(EnumJob.LUMBERJACK);
    }

    @Override
    public void addParts(LinkedList<String> parts) {
        parts.add("Hello, " + mc.player.getName() + "!");
        parts.add("Want to gather wood for the city and keep up the forest?");
        parts.add("Being a lumberjack is very very hard work.");
        parts.add("We are required to plant saplings back after we cut down a tree.");
        parts.add("You can turn in any logs or saplings you find to me for cash and I will give them to the city!");
        parts.add("Do you want to be a lumberjack?");
    }
}