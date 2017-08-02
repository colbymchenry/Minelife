package com.minelife.drug.client.gui;

import buildcraft.core.lib.gui.BuildCraftContainer;
import buildcraft.core.lib.gui.slots.SlotOutput;
import com.minelife.drug.tileentity.TileEntityAmmoniaExtractor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerAmmoniaExtractor extends BuildCraftContainer {

    private final TileEntityAmmoniaExtractor tile_ammonia_extractor;
    private int last_progress;

    // TODO: Align with GUI
    public ContainerAmmoniaExtractor(InventoryPlayer inventory_player, TileEntityAmmoniaExtractor tile_ammonia_extractor) {
        super(tile_ammonia_extractor.getSizeInventory());
        this.tile_ammonia_extractor = tile_ammonia_extractor;
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
        this.addSlotToContainer(new SlotOutput(tile_ammonia_extractor, 0, 107, 41));
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);
        icrafting.sendProgressBarUpdate(this, 0, this.tile_ammonia_extractor.progress());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (Object crafter : this.crafters) {
            ICrafting icrafting = (ICrafting) crafter;
            if (this.last_progress != this.tile_ammonia_extractor.progress()) {
                icrafting.sendProgressBarUpdate(this, 3, this.tile_ammonia_extractor.progress());
            }

            this.tile_ammonia_extractor.sendGUINetworkData(this, (ICrafting) crafter);
        }

        this.last_progress = this.tile_ammonia_extractor.progress();
    }

    @Override
    public void updateProgressBar(int id, int data) {
        this.tile_ammonia_extractor.getGUINetworkData(id, data);
        if(id == 3) this.tile_ammonia_extractor.set_progress(data);
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return this.tile_ammonia_extractor.isUseableByPlayer(entityplayer);
    }
}

