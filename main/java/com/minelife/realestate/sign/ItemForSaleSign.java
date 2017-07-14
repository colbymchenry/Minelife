package com.minelife.realestate.sign;

import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemForSaleSign extends Item {

    private static ItemForSaleSign item;

    private ItemForSaleSign()
    {
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setTextureName(Minelife.MOD_ID + ":SaleSign");
    }

    public static ItemForSaleSign getItem() {
        if(item == null) item = new ItemForSaleSign();
        return item;
    }

    @Override
    public boolean onItemUse(ItemStack heldStack, EntityPlayer player, World world, int xPosition, int yPosition, int zPosition, int side, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
        if (side == 0) {
            return false;
        } else if (!world.getBlock(xPosition, yPosition, zPosition).getMaterial().isSolid()) {
            return false;
        } else {
            if (side == 1) {
                ++yPosition;
            }

            if (side == 2) {
                --zPosition;
            }

            if (side == 3) {
                ++zPosition;
            }

            if (side == 4) {
                --xPosition;
            }

            if (side == 5) {
                ++xPosition;
            }

            if (!player.canPlayerEdit(xPosition, yPosition, zPosition, side, heldStack)) {
                return false;
            } else if (!Blocks.standing_sign.canPlaceBlockAt(world, xPosition, yPosition, zPosition)) {
                return false;
            } else if (world.isRemote) {
                return true;
            } else {
                if (side == 1) {
                    int i1 = MathHelper.floor_double((double) ((player.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                    world.setBlock(xPosition, yPosition, zPosition, BlockForSaleSign.getBlock(true), i1, 3);
                } else {
                    world.setBlock(xPosition, yPosition, zPosition, BlockForSaleSign.getBlock(false), side, 3);
                }

                --heldStack.stackSize;
                TileEntityForSaleSign tileentitysign = (TileEntityForSaleSign) world.getTileEntity(xPosition, yPosition, zPosition);

                if (tileentitysign != null) {
                    player.func_146100_a(tileentitysign);
                }

                return true;
            }
        }
    }

}
