package com.minelife.drug.client.gui;

import com.minelife.drug.tileentity.TileEntityDryingRack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDryingRack extends Container {

    private final TileEntityDryingRack tile_drying_rack;

    // TODO: Align with GUI
    public ContainerDryingRack(InventoryPlayer inventory_player, TileEntityDryingRack tile_drying_rack) {
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
        for(int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(tile_drying_rack, i, 8 + i * 18, 13));
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

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            // into drying rack
            if (par2 < 9) {
                // try to place in player inventory / action bar
                if (!this.mergeItemStack(itemstack1, INV_START, HOTBAR_END + 1, true)) {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            // Item is in inventory / hotbar, try to place either in eye or armor slots
            else {
                // if item is a sharingan eye
                if (itemstack1.getItem() instanceof ItemScroll) {
                    if (!this.mergeItemStack(itemstack1, 0, ACTIVE_SLOT, false)) {
                        return null;
                    }
                }
                // if item is armor
                else if (itemstack1.getItem() instanceof ItemArmor) {
                    int type = ((ItemArmor) itemstack1.getItem()).armorType;
                    if (!this.mergeItemStack(itemstack1, ARMOR_START + type, ARMOR_START + type + 1, false)) {
                        return null;
                    }
                }
                // item in player's inventory, but not in action bar
                else if (par2 >= INV_START && par2 < HOTBAR_START) {
                    // place in action bar
                    if (!this.mergeItemStack(itemstack1, HOTBAR_START, HOTBAR_START + 1, false)) {
                        return null;
                    }
                }
                // item in action bar - place in player inventory
                else if (par2 >= HOTBAR_START && par2 < HOTBAR_END + 1) {
                    if (!this.mergeItemStack(itemstack1, INV_START, INV_END + 1, false)) {
                        return null;
                    }
                }
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack) null);
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
