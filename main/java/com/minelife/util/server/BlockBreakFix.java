package com.minelife.util.server;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent;

public class BlockBreakFix {

    public static void onBreak(BlockEvent.BreakEvent event) {
        TileEntity tileEntity = event.world.getTileEntity(event.x, event.y, event.z);
        if (tileEntity != null) {
            tileEntity.getWorldObj().markBlockForUpdate(event.x, event.y, event.z);
            tileEntity.markDirty();
        }
    }

}
