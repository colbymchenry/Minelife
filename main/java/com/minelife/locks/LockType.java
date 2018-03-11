package com.minelife.locks;

public enum LockType {

    IRON(90, 100), GOLD(95, 100), DIAMOND(98, 100), OBSIDIAN(99, 100);

    public int min, max;

    LockType(int min, int max) {
        this.min = min;
        this.max = max;
    }

}
