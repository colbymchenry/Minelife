package com.minelife.locks;

public enum LockType {

    IRON(990, 1000), GOLD(992, 1000), DIAMOND(998, 1000), OBSIDIAN(995, 1000);

    public int min, max;

    LockType(int min, int max) {
        this.min = min;
        this.max = max;
    }

}
