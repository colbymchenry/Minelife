package com.minelife.drug.client.gui;

import buildcraft.core.lib.gui.BuildCraftContainer;
import buildcraft.core.lib.gui.slots.SlotOutput;
import com.minelife.drug.tileentity.TileEntityPresser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerPresser extends BuildCraftContainer {

    private TileEntityPresser tile_presser;

    public ContainerPresser(InventoryPlayer inventory_player, TileEntityPresser tile_presser)
    {
        super(tile_presser.getSizeInventory());
        this.tile_presser = tile_presser;

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

        // add input slot for item
        this.addSlotToContainer(new Slot(tile_presser, 0, 52, 41));
        // add output slot
        this.addSlotToContainer(new SlotOutput(tile_presser, 1, 107, 41));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return this.tile_presser.isUseableByPlayer(player);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (Object crafter : this.crafters) {
            this.tile_presser.sendGUINetworkData(this, (ICrafting) crafter);
        }

    }

    @Override
    public void updateProgressBar(int i, int j)
    {
        this.tile_presser.getGUINetworkData(i, j);
    }


}
