package com.minelife.drugs.client.gui;

import buildcraft.lib.gui.ContainerBCTile;
import buildcraft.lib.gui.slot.SlotBase;
import buildcraft.lib.gui.slot.SlotOutput;
import com.minelife.drugs.tileentity.TileEntityPresser;
import com.minelife.drugs.tileentity.TileEntityVacuum;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerPresser extends ContainerBCTile<TileEntityPresser> {

    public ContainerPresser(EntityPlayer player, TileEntityPresser tile) {
        super(player, tile);

        // add input slot for item
        this.addSlotToContainer(new SlotBase(tile.invMaterials, 0, 52, 41));
        // add output slot
        this.addSlotToContainer(new SlotOutput(tile.invResult, 0, 107, 41));

        addFullPlayerInventory(84);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

}