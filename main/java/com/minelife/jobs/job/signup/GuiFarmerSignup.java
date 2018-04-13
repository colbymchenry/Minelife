package com.minelife.jobs.job.signup;

import com.minelife.jobs.EnumJob;

import java.util.LinkedList;

public class GuiFarmerSignup extends GuiSignUp {


    public GuiFarmerSignup() {
        super(EnumJob.FARMER);
    }

    @Override
    public void addParts(LinkedList<String> parts) {
        parts.add("Hello, " + mc.player.getName() + "!");
        parts.add("Thinking of becoming an ole farmer like me eh?");
        parts.add("Well being a farmer isn't as easy as it looks.");
        parts.add("We work day in and day out to provide food and crops for the city.");
        parts.add("Scraping up our hands in the hot sun and planting seeds back in the rich soil.");
        parts.add("You can sell almost any crop, fruit, or flower to me if you have them on you!");
        parts.add("Do you want to be a farmer?");
    }
}