package com.minelife.economy.client.wallet;

import com.minelife.economy.ItemMoney;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotWallet extends Slot {

    public SlotWallet(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
        super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
    }

    @Override
    public boolean isItemValid(ItemStack itemstack)
    {
        // Everything returns true except an instance of our Item
        return itemstack.getItem() instanceof ItemMoney;
    }
}
