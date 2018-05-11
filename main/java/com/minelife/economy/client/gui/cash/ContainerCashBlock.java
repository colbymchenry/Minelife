package com.minelife.economy.client.gui.cash;

import com.minelife.economy.ModEconomy;
import com.minelife.economy.tileentity.TileEntityCash;
import com.minelife.police.client.ContainerPlayerInventory;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

public class ContainerCashBlock extends Container {

    private final int numRows;
    private final TileEntityCash tileCash;

    public ContainerCashBlock(IInventory playerInventory, IInventory inventory, TileEntityCash tileCash, EntityPlayer player) {
        this.numRows = inventory.getSizeInventory() / 9;
        this.tileCash = tileCash;
        inventory.openInventory(player);
        int i = (this.numRows - 4) * 18;

        for (int j = 0; j < this.numRows; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlotToContainer(new SlotCash(inventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlotToContainer(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (tileCash != null)
            tileCash.sendUpdates();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.numRows * 9)
            {
                if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        System.out.println("BOOM");
        System.out.println(clickTypeIn.name());
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    class SlotCash extends Slot {

        public SlotCash(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack.getItem() == ModEconomy.itemCash;
        }
    }

}