package com.minelife.drug.client.gui;

import com.minelife.MLItems;
import com.minelife.drug.tileentity.TileEntityDryingRack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDryingRack extends Container {

    private final TileEntityDryingRack tile_drying_rack;

    public ContainerDryingRack(InventoryPlayer inventory_player, TileEntityDryingRack tile_drying_rack)
    {
        this.tile_drying_rack = tile_drying_rack;
        // Player Inventory, Slot 9-35, Slot IDs 9-35
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlotToContainer(new Slot(inventory_player, x + y * 9 + 9, 8 + x * 18, 44 + y * 18));
            }
        }

        // Player Inventory, Slot 0-8, Slot IDs 36-44
        for (int x = 0; x < 9; ++x) {
            this.addSlotToContainer(new Slot(inventory_player, x, 8 + x * 18, 102));
        }

        // add output slot
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new DryingRackSlot(tile_drying_rack, i, 8 + i * 18, 13));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return tile_drying_rack.isUseableByPlayer(player);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(par2);

        boolean inside_drying_rack = par2 >= 36 && par2 <= 45;
        boolean inside_hotbar = par2 >= 27 && par2 <= 35;
        boolean inside_inventory = par2 >= 0 && par2 <= 26;

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if(inside_drying_rack) {
                if (!this.mergeItemStack(itemstack1, 0, 35, true)) {
                    return null;
                }
            } else  {
                if (!this.mergeItemStack(itemstack1, 36, 45, true)) {
                    return null;
                }
            }

            slot.onSlotChange(itemstack1, itemstack);

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }

        return itemstack;
    }

    private class DryingRackSlot extends Slot {

        public DryingRackSlot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_)
        {
            super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
        }

        @Override
        public boolean isItemValid(ItemStack p_75214_1_)
        {
            return p_75214_1_ != null && p_75214_1_.getItem() == MLItems.coca_leaf;
        }
    }
}
