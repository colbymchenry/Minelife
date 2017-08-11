package com.minelife.drug;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;

import java.util.HashMap;
import java.util.Map;

public final class BucketHandler {

    public static BucketHandler INSTANCE = new BucketHandler();
    public Map<Block, Item> buckets = new HashMap();

    private BucketHandler() {
    }

    @SubscribeEvent
    public void onBucketFill(FillBucketEvent event) {
        ItemStack result = this.fillCustomBucket(event.world, event.target);
        if (result != null) {
            event.result = result;
            event.setResult(Event.Result.ALLOW);
            event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, result);
        }
    }

    private ItemStack fillCustomBucket(World world, MovingObjectPosition pos) {
        Block block = world.getBlock(pos.blockX, pos.blockY, pos.blockZ);
        Item bucket = (Item)this.buckets.get(block);
        if (bucket != null && world.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ) == 0) {
            world.setBlockToAir(pos.blockX, pos.blockY, pos.blockZ);
            return new ItemStack(bucket);
        } else {
            return null;
        }
    }
}