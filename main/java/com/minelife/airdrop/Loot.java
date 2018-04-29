package com.minelife.airdrop;

import com.minelife.util.ItemHelper;
import net.minecraft.item.ItemStack;

public class Loot {

    public ItemStack itemStack;
    public int weight;

    public Loot(ItemStack itemStack, int weight) {
        this.itemStack = itemStack;
        this.weight = weight;
    }

    public String toString() {
        return ItemHelper.itemToString(itemStack) + ";" + weight;
    }

    public static Loot fromString(String str) {
        String[] data = str.split(";");
        ItemStack stack = ItemHelper.itemFromString(data[0]);
        int weight = Integer.parseInt(data[1]);
        return new Loot(stack, weight);
    }

}
