package com.minelife.realestate.sign;

import net.minecraft.block.BlockSign;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Random;

public class BlockForSaleSign extends BlockSign {

    private static BlockForSaleSign standing, wall_mounted;

    public BlockForSaleSign(boolean standing)
    {
        super(TileEntityForSaleSign.class, standing);
    }

    public static BlockForSaleSign getBlock(boolean standing) {
        if(standing) {
            if(BlockForSaleSign.standing == null) BlockForSaleSign.standing = new BlockForSaleSign(true);
            return BlockForSaleSign.standing;
        } else {
            if(BlockForSaleSign.wall_mounted == null) BlockForSaleSign.wall_mounted = new BlockForSaleSign(false);
            return BlockForSaleSign.wall_mounted;
        }
    }

    // TODO:
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack heldItem)
    {
        super.onBlockPlacedBy(world, x, y, z, entity, heldItem);
    }

    // TODO
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f, float f1, float f2)
    {
        return super.onBlockActivated(world, x, y, z, player, side, f, f1, f2);
    }

    @Override
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return ItemForSaleSign.getItem();
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return ItemForSaleSign.getItem();
    }
}
