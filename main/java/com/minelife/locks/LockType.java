package com.minelife.locks;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public enum LockType {

    IRON(98, Items.IRON_INGOT),
    GOLD(99, Items.GOLD_INGOT),
    DIAMOND(99.8, Items.DIAMOND),
    OBSIDIAN(99.5, Item.getItemFromBlock(Blocks.OBSIDIAN)),
    EMERALD(99.5, Items.EMERALD),
    BEDROCK(101, Item.getItemFromBlock(Blocks.BEDROCK));

    private static Random rand = new Random();
    public double chance;
    public Item itemResource;

    LockType(double chance, Item item) {
        this.chance = chance;
        this.itemResource = item;
    }

    public boolean tryToUnlock() {
        return MathHelper.nextDouble(rand, 0.0D, 100.0D) > this.chance;
    }
}
