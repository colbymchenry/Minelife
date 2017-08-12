package com.minelife.drug.client.gui;

import buildcraft.core.lib.gui.BuildCraftContainer;
import buildcraft.core.lib.gui.slots.SlotOutput;
import com.minelife.drug.tileentity.TileEntityCementMixer;
import com.minelife.drug.tileentity.TileEntityLeafMulcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCementMixer extends BuildCraftContainer {

    private TileEntityCementMixer tile_cement_mixer;
    private int last_progress;
    private ItemStack prev_output;

    public ContainerCementMixer(InventoryPlayer inventory_player, TileEntityCementMixer tile_cement_mixer)
    {
        super(tile_cement_mixer.getSizeInventory());
        this.tile_cement_mixer = tile_cement_mixer;

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

        // add input slots for items
        int slot_count = 0;
        for(int x = 0; x < 3; ++x) {
            for(int y = 0; y < 3; ++y) {
                this.addSlotToContainer(new Slot(tile_cement_mixer, slot_count++, 36 + x * 18, 16 + y * 18));
            }
        }
        // add output slot
        this.addSlotToContainer(new SlotOutput(tile_cement_mixer, 9, 130, 34));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return this.tile_cement_mixer.isUseableByPlayer(player);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (Object crafter : this.crafters) {
            this.tile_cement_mixer.sendGUINetworkData(this, (ICrafting) crafter);
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafter)
    {
        super.addCraftingToCrafters(crafter);
        this.tile_cement_mixer.sendGUINetworkData(this, crafter);
    }

    @Override
    public void updateProgressBar(int i, int j)
    {
        this.tile_cement_mixer.getGUINetworkData(i, j);
    }

}
