package com.minelife.realestate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
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

        if(estate == null) {
            player.addChatComponentMessage(new ChatComponentText("There is no estate in this chunk."));
            return false;
        }

        if(!estate.getOwner().equals(player.getUniqueID())) {
            player.addChatComponentMessage(new ChatComponentText("You do not own this estate."));
            return false;
        }

        NBTTagCompound tagCompound = heldItem.hasTagCompound() ? heldItem.getTagCompound() : new NBTTagCompound();
        if(tagCompound.hasKey("estates")) {

        } else {
//            tagCompound.setString("estates", estate.);
        }

        return true;
    }

}
