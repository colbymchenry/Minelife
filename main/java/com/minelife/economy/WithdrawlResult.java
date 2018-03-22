package com.minelife.economy;

import net.minecraft.item.ItemStack;

import java.util.List;

public class WithdrawlResult {

    List<ItemStack> stacksTaken;
    int changeThatDidNotFit;

    public WithdrawlResult(List<ItemStack> stacksTaken, int changeThatDidNotFit) {
        this.stacksTaken = stacksTaken;
        this.changeThatDidNotFit = changeThatDidNotFit;
    }
}
