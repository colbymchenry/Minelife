package com.minelife.economy.client.gui.wallet;

import com.minelife.economy.client.gui.cash.ContainerCashBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.EnumHand;

public class ContainerWallet extends ContainerCashBlock {

    private InventoryWallet inventoryWallet;

    public ContainerWallet(IInventory playerInventory, InventoryWallet inventoryWallet, EntityPlayer player) {
        super(playerInventory, inventoryWallet.getInventory(), null, player);
        this.inventoryWallet = inventoryWallet;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        inventoryWallet.writeToNBT();
        playerIn.setHeldItem(EnumHand.MAIN_HAND, inventoryWallet.getWalletStack());
    }

}
