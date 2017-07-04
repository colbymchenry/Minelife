package com.minelife.realestate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ItemChunkSelector extends Item {

    // TODO
    @Override
    public boolean onItemUse(ItemStack heldItem, EntityPlayer player, World world, int x, int y, int z, int side, float f, float f1, float f2)
    {
        if(world.isRemote) return false;

        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        Estate estate = Estate.getEstate(world, x, z);

        if(estate != null) {

        }

        return true;
    }
}
