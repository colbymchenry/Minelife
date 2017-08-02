package com.minelife.drug.client.gui;

import com.minelife.drug.tileentity.TileEntityDryingRack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerDryingRack extends Container {

    private final TileEntityDryingRack tile_drying_rack;
    private int last_progress;

    // TODO: Align with GUI
    public ContainerDryingRack(InventoryPlayer inventory_player, TileEntityDryingRack tile_drying_rack) {
        this.tile_drying_rack = tile_drying_rack;
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

        // add output slot
        for(int i = 0; i < 9; i++) {
            // TODO: Adjust x and y based off of gui
            this.addSlotToContainer(new Slot(tile_drying_rack, i, 107, 41));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return tile_drying_rack.isUseableByPlayer(player);
    }
}
