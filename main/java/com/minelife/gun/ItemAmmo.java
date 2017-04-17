package com.minelife.gun;

import net.minecraft.item.Item;

public abstract class ItemAmmo extends Item {

    private final int damage;

    public ItemAmmo(int damage) {
        this.damage = damage;
    }
}
