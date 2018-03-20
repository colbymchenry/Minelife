package com.minelife.economy.item;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.tileentity.TileEntityCash;
import com.minelife.util.NumberConversions;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCash extends Item {

    public ItemCash() {
        setRegistryName("cash");
        setMaxDamage(0);
        setUnlocalizedName(Minelife.MOD_ID + ":cash");
        setHasSubtypes(true);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return EnumActionResult.SUCCESS;

        if(worldIn.getTileEntity(pos) instanceof TileEntityCash) {
            TileEntityCash tile = (TileEntityCash) worldIn.getTileEntity(pos);
            ItemStack newStack = player.getHeldItem(hand).copy();
            int newStackSize = tile.deposit(newStack);
            if(newStackSize < 1) {
                player.setHeldItem(hand, ItemStack.EMPTY);
            } else {
                newStack.setCount(newStackSize);
                player.setHeldItem(hand, newStack);
            }
            tile.sendUpdates();
            return EnumActionResult.SUCCESS;
        }

        switch (facing) {
            case DOWN:
                pos = pos.add(0, -1, 0);
                break;
            case UP:
                pos = pos.add(0, 1, 0);
                break;
            case NORTH:
                pos = pos.add(0, 0, -1);
                break;
            case SOUTH:
                pos = pos.add(0, 0, 1);
                break;
            case WEST:
                pos = pos.add(-1, 0, 0);
                break;
            case EAST:
                pos = pos.add(1, 0, 0);
                break;
        }

        worldIn.setBlockState(pos, ModEconomy.blockCash.getDefaultState());
        TileEntityCash tile = (TileEntityCash) worldIn.getTileEntity(pos);
        tile.deposit(player.getHeldItem(hand));
        tile.sendUpdates();
        player.setHeldItem(hand, ItemStack.EMPTY);
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        for (int i = 0; i < 6; i++) {
            ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Minelife.MOD_ID + ":cash_" + i, "inventory");
            ModelLoader.setCustomModelResourceLocation(this, i, itemModelResourceLocation);
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(tab != CreativeTabs.MISC) return;
        for (int i = 0; i < 6; i++) {
            ItemStack subItemStack = new ItemStack(this, 1, i);
            items.add(subItemStack);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getMetadata();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return "$" + NumberConversions.format(getAmount(stack));
    }

    public static int getAmount(ItemStack stack) {
        if(stack != null && stack.getItem() == ModEconomy.itemCash) {
            switch(stack.getMetadata()) {
                case 0: return stack.getCount();
                case 1: return stack.getCount() * 5;
                case 2: return stack.getCount() * 10;
                case 3: return stack.getCount() * 20;
                case 4: return stack.getCount() * 50;
                case 5: return stack.getCount() * 100;
            }
        }
        return 0;
    }
}