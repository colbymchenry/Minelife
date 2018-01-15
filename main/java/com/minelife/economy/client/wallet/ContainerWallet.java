package com.minelife.economy.client.wallet;

import buildcraft.core.lib.gui.BuildCraftContainer;
import buildcraft.core.lib.inventory.SimpleInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import com.minelife.economy.client.wallet.InventoryWallet;
import net.minecraft.item.ItemStack;

public class ContainerWallet extends BuildCraftContainer {

    private InventoryPlayer InventoryPlayer;
    private InventoryWallet WalletInventory;

    public ContainerWallet(net.minecraft.entity.player.InventoryPlayer inventoryPlayer, InventoryWallet WalletInventory) {
        super(WalletInventory.getSizeInventory());
        this.InventoryPlayer = inventoryPlayer;
        this.WalletInventory = WalletInventory;

        // Tile Entity, Slot 0-8, Slot IDs 0-8
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                this.addSlotToContainer(new SlotWallet(WalletInventory, x + y * 3, 62 + x * 18, 17 + y * 18));
            }
        }

        // Player Inventory, Slot 9-35, Slot IDs 9-35
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlotToContainer(new Slot(InventoryPlayer, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        // Player Inventory, Slot 0-8, Slot IDs 36-44
        for (int x = 0; x < 9; ++x) {
            this.addSlotToContainer(new Slot(InventoryPlayer, x, 8 + x * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }

    @Override
    protected boolean mergeItemStack(ItemStack p_75135_1_, int p_75135_2_, int p_75135_3_, boolean p_75135_4_) {
        boolean value = super.mergeItemStack(p_75135_1_, p_75135_2_, p_75135_3_, p_75135_4_);
        WalletInventory.markDirty();
        return value;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        WalletInventory.markDirty();
        player.inventory.setInventorySlotContents(player.inventory.currentItem, WalletInventory.WalletStack);
    }
}
