package com.minelife.locks;

import java.util.Random;

public enum LockType {

    IRON(98), GOLD(99), DIAMOND(99.8), OBSIDIAN(99.5), EMERALD(99.5), BEDROCK(101);

    private static Random rand = new Random();
    public double chance;

    LockType(double chance) {
        this.chance = chance;
    }

    public boolean tryToUnlock() {
        return rand.nextInt(100) > this.chance;
    }
}
