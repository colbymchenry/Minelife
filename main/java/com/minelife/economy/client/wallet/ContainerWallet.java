package com.minelife.economy.client.wallet;

import buildcraft.core.lib.gui.BuildCraftContainer;
import buildcraft.core.lib.inventory.SimpleInventory;
import com.minelife.economy.ItemWallet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerChest;
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

        int numRows = WalletInventory.getSizeInventory() / 9;
        int i = (numRows - 4) * 18;
        int j;
        int k;

        for (j = 0; j < numRows; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new SlotWallet(WalletInventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (j = 0; j < 3; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new Slot(inventoryPlayer, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }

        for (j = 0; j < 9; ++j)
        {
            this.addSlotToContainer(new Slot(inventoryPlayer, j, 8 + j * 18, 161 + i));
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
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack itemStack = player.inventory.mainInventory[i];
            if(itemStack != null && itemStack.getItem() instanceof ItemWallet) {
                if(itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("UUID") && itemStack.getTagCompound().getString("UUID").equals(WalletInventory.WalletStack.getTagCompound().getString("UUID"))) {
                    player.inventory.setInventorySlotContents(i, WalletInventory.WalletStack);
                    break;
                }
            }
        }
    }
}
