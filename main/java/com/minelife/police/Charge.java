package com.minelife.police;

public class Charge {

    public int counts, bail;
    public String description;

    public Charge(int counts, int bail, String description) {
        this.counts = counts;
        this.bail = bail;
        this.description = description;
    }

    @Override
    public String toString() {
        return counts + "," + bail + "," + description;
    }

    public static Charge fromString(String str) {
        String[] data = str.split(",");
        int counts = Integer.parseInt(data[0]);
        int bail = Integer.parseInt(data[1]);
        String description = data[2];
        return new Charge(counts, bail, description);
    }
}
