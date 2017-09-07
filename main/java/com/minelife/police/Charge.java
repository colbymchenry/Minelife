package com.minelife.police;

import com.google.common.collect.Lists;

import java.util.List;

public class Charge {

    // jailTime is in minutes, 20 minutes = 1 mc day
    public int counts, bail, jailTime;
    public String description;

    public Charge(int counts, int bail, int jailTime, String description) {
        this.counts = counts;
        this.bail = bail;
        this.jailTime = jailTime;
        this.description = description;
    }

    @Override
    public String toString() {
        return counts + "," + bail + "," + jailTime + "," + description;
    }

    public static Charge fromString(String str) {
        String[] data = str.split(",");
        if(data.length < 4) return new Charge(0, 0, 0, "null");
        int counts = data[0].isEmpty() ? 0 : Integer.parseInt(data[0]);
        int bail = data[1].isEmpty() ? 0 : Integer.parseInt(data[1]);
        int jailTime = data[2].isEmpty() ? 0 : Integer.parseInt(data[2]);
        String description = data[3];
        return new Charge(counts, bail, jailTime, description);
    }

    public static List<Charge> getDefaultCharges() {
        List<Charge> chargeList = Lists.newArrayList();
        chargeList.add(new Charge(1, 1000, 20 * 6,"Griefing"));
        chargeList.add(new Charge(1, 200, 20,"Marijuana"));
        chargeList.add(new Charge(1, 800, 20 * 3,"Cocaine"));
        chargeList.add(new Charge(1, 100000, 20 * 12,"Murder"));
        chargeList.add(new Charge(1, 400, 20 * 2,"Selling Marijuana"));
        chargeList.add(new Charge(1, 1600, 20 * 6,"Selling Cocaine"));
        return chargeList;
    }

}
