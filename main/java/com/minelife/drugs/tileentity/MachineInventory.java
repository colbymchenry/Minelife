package com.minelife.drugs.tileentity;

import buildcraft.lib.tile.item.IItemHandlerAdv;
import com.minelife.util.MLInventory;
import net.minecraftforge.items.IItemHandlerModifiable;

public abstract class MachineInventory extends MLInventory implements IItemHandlerModifiable, IItemHandlerAdv {

    public MachineInventory(int size, String name, int maxStackSize) {
        super(size, name, maxStackSize);
    }

}
