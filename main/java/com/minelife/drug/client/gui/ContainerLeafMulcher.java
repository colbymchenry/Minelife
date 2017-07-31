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
        int slot = this.setup_player_slots(inventory_player) + 1;
        // add input slot for fuel
        this.addSlotToContainer(new Slot(this.tile_leaf_mulcher, slot++, 124, 35));
        // add input slot for item
        this.addSlotToContainer(new Slot(this.tile_leaf_mulcher, slot++, 124, 35));
        // add output slot
        this.addSlotToContainer(new SlotOutput(this.tile_leaf_mulcher, slot, 124, 35));
        this.onCraftMatrixChanged(this.tile_leaf_mulcher);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        Iterator var1 = this.crafters.iterator();

        while(var1.hasNext()) {
            Object crafter = var1.next();
            this.engine.sendGUINetworkData(this, (ICrafting)crafter);
        }

    }

    @Override
    public void updateProgressBar(int i, int j) {
        this.engine.getGUINetworkData(i, j);
    }


    public int setup_player_slots(InventoryPlayer inventory_player) {
        int x;
        int slot = 0;
        // players hot bar
        for(x = 0; x < 9; ++x) {
            slot++;
            this.addSlotToContainer(new Slot(inventory_player, x, 8 + x * 18, 173));
        }

        // players main inventory
        for(x = 0; x < 3; ++x) {
            for(x = 0; x < 9; ++x) {
                slot++;
                this.addSlotToContainer(new Slot(inventory_player, x + x * 9 + 9, 8 + x * 18, 115 + x * 18));
            }
        }

        return slot;
    }
}
