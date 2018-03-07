package com.minelife.casino.slotmachine;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSlotMachine extends BlockContainer {

    public BlockSlotMachine() {
        super(Material.iron);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySlotMachine();
    }

}
