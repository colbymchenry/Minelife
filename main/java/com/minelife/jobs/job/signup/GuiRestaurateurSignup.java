package com.minelife.jobs.job.signup;

import com.minelife.jobs.EnumJob;

import java.util.LinkedList;

public class GuiRestaurateurSignup extends GuiSignUp {

    public GuiRestaurateurSignup() {
        super(EnumJob.RESTAURATEUR);
    }

    @Override
    public void addParts(LinkedList<String> parts) {
        parts.add("Hello, " + mc.player.getName() + "!");
        parts.add("Thinking of starting your own restaurant?");
        parts.add("Owning a restaurant is a lot of fun, but it's also very tricky.");
        parts.add("You will have the opportunity to order supplies and choose to store your cash in...");
        parts.add("your restaurant or cash it out. However, the more you store in your restaurant...");
        parts.add("the more you will be able to order at one time and thus produce more at one time.");
        parts.add("Would you like to become a restaurateur?");
    }
}
