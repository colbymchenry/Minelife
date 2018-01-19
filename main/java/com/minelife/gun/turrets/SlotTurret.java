package com.minelife.gun.turrets;

import com.minelife.MLItems;
import com.minelife.gun.item.ammos.ItemAmmo;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotTurret extends Slot {

    public SlotTurret(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
        super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
    }

    @Override
    public boolean isItemValid(ItemStack p_75214_1_) {
        return p_75214_1_ != null && p_75214_1_.getItem() == MLItems.ammo_pistol;
    }
}
