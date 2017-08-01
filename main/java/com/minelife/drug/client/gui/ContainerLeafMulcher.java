package com.minelife.drug.client.gui;

import buildcraft.core.lib.gui.BuildCraftContainer;
import buildcraft.core.lib.gui.slots.SlotOutput;
import com.minelife.drug.tileentity.TileEntityLeafMulcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

import java.util.Iterator;

public class ContainerLeafMulcher extends BuildCraftContainer {

    private TileEntityLeafMulcher tile_leaf_mulcher;

    public ContainerLeafMulcher(InventoryPlayer inventory_player, TileEntityLeafMulcher tile_leaf_mulcher)
    {
        super(tile_leaf_mulcher.getSizeInventory());
        this.tile_leaf_mulcher = tile_leaf_mulcher;

        // Player Inventory, Slot 9-35, Slot IDs 9-35
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlotToContainer(new Slot(inventory_player, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        // Player Inventory, Slot 0-8, Slot IDs 36-44
        for (int x = 0; x < 9; ++x) {
            this.addSlotToContainer(new Slot(inventory_player, x, 8 + x * 18, 142));
        }

        // add input slot for fuel
        this.addSlotToContainer(new Slot(tile_leaf_mulcher, 0, 132, 61));
        // add input slot for item
        this.addSlotToContainer(new Slot(tile_leaf_mulcher, 1, 52, 41));
        // add output slot
        this.addSlotToContainer(new SlotOutput(tile_leaf_mulcher, 2, 107, 41));
//        this.onCraftMatrixChanged(this.tile_leaf_mulcher);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return this.tile_leaf_mulcher.isUseableByPlayer(player);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (Object crafter : this.crafters) {
            this.tile_leaf_mulcher.sendGUINetworkData(this, (ICrafting) crafter);
        }

    }

    @Override
    public void updateProgressBar(int i, int j) {
        this.tile_leaf_mulcher.getGUINetworkData(i, j);
    }

}
