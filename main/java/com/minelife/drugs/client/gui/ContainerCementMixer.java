package com.minelife.drugs.client.gui;

import buildcraft.lib.gui.ContainerBCTile;
import buildcraft.lib.gui.slot.SlotBase;
import buildcraft.lib.gui.slot.SlotOutput;
import buildcraft.lib.gui.widget.WidgetFluidTank;
import com.minelife.drugs.tileentity.TileEntityCementMixer;
import com.minelife.drugs.tileentity.TileEntityLeafMulcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerCementMixer extends ContainerBCTile<TileEntityCementMixer> {

    public final WidgetFluidTank widgetTankFuel;

    public ContainerCementMixer(EntityPlayer player, TileEntityCementMixer tile) {
        super(player, tile);

        // add input slots for item
        int slot_count = 0;
        for(int x = 0; x < 3; ++x) {
            for(int y = 0; y < 3; ++y) {
                this.addSlotToContainer(new SlotBase(tile.invMaterials, slot_count++, 52 + x * 18, 16 + y * 18));
            }
        }

        // add output slot
        this.addSlotToContainer(new SlotOutput(tile.invResult, 0, 146, 34));

        addFullPlayerInventory(30, 84);

        widgetTankFuel = addWidget(new WidgetFluidTank(this, tile.tankFuel));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

}