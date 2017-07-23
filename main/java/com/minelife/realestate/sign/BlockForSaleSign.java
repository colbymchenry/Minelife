package com.minelife.realestate.sign;

import com.minelife.realestate.Zone;
import com.minelife.realestate.client.GuiZonePurchase;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

import java.util.Random;

public class BlockForSaleSign extends BlockSign {

    private static BlockForSaleSign standing, wall_mounted;

    public BlockForSaleSign(boolean standing)
    {
        super(TileEntityForSaleSign.class, standing);
        this.setBlockUnbreakable();
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

    // prevents the block from falling like a normal sign if it has no post
    @Override
    public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        if(world.isRemote) return true;
        Zone zone = Zone.getZone(world, Vec3.createVectorHelper(x, y, z));
        if(zone == null) return false;
        if(zone.hasForSaleSign(Side.SERVER)) return false;
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f, float f1, float f2)
    {
        if(world.isRemote) return true;
        TileEntityForSaleSign forSaleSign = (TileEntityForSaleSign) world.getTileEntity(x, y, z);
        if(forSaleSign.isOccupied()) return false;
        GuiZonePurchase.PacketOpenGuiZonePurchase.openFromServer(forSaleSign, (EntityPlayerMP) player);
        return super.onBlockActivated(world, x, y, z, player, side, f, f1, f2);
    }

    @SideOnly(Side.CLIENT)
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
