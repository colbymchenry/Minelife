package com.minelife.drug.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDryingRack extends BlockContainer {

    private static BlockDryingRack instance;

    private BlockDryingRack()
    {
        super(Material.wood);
    }

    public static BlockDryingRack instance() {
        if(instance == null) instance = new BlockDryingRack();
        return instance;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return null;
    }
}
