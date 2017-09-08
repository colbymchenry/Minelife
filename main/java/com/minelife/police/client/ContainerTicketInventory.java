package com.minelife.police.client;

import com.minelife.police.TicketInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerTicketInventory extends Container {

    private final TicketInventory ticketInventory;

    public ContainerTicketInventory(InventoryPlayer inventory_player, TicketInventory ticketInventory) {
        this.ticketInventory = ticketInventory;
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
            this.addSlotToContainer(new Slot(ticketInventory, i, 8 + i * 18, 13));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return ticketInventory.isUseableByPlayer(player);
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (!player.worldObj.isRemote) {
            ticketInventory.closeInventory();
            ticketInventory.updateCreative((EntityPlayerMP) player);
        }
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(par2);

        boolean inside_drying_rack = par2 >= 36 && par2 <= 45;
        boolean inside_hotbar = par2 >= 27 && par2 <= 35;
        boolean inside_inventory = par2 >= 0 && par2 <= 26;

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (inside_drying_rack) {
                if (!this.mergeItemStack(itemstack1, 0, 35, true)) {
                    return null;
                }
            } else {
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

}
