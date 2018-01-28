package com.minelife.economy.cash;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.Lists;
import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.economy.ItemMoney;
import com.minelife.economy.ItemWallet;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.List;

public class BlockCash extends BlockContainer {

    private IIcon icon;

    public BlockCash() {
        super(Material.iron);
        GameRegistry.registerTileEntity(TileEntityCash.class, "tileCash");
        setBlockName("cash");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (!world.isRemote) {
            if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemMoney) {
                if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityCash) {
                    ItemStack stack = player.getHeldItem().copy();
                    int oldStackSize = stack.stackSize;
                    int newStackSize = ((TileEntityCash) world.getTileEntity(x, y, z)).addCash(player.getHeldItem());
                    if(oldStackSize == newStackSize) {
                        player.openGui(Minelife.instance, 80099, world, x, y, z);
                    } else {
                        stack.stackSize = newStackSize;
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, stack.stackSize > 0 ? stack : null);
                        ((TileEntityCash) world.getTileEntity(x, y, z)).Sync();
                    }
                }
            } else {
                player.openGui(Minelife.instance, 80099, world, x, y, z);
            }
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCash();
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return -1;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_) {
        // TODO: Maybe instead implement the bag of cash and just drop the bag to prevent too many entities
        Vector3 vec3 = new Vector3(x, y, z);
        if(world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityCash) {
            List<ItemStack> cashStacks = Lists.newArrayList();
            for (int i = 0; i < ((TileEntityCash) world.getTileEntity(x, y, z)).getSizeInventory(); i++) {
                if(((TileEntityCash) world.getTileEntity(x, y, z)).getStackInSlot(i) != null) {
                    cashStacks.add(((TileEntityCash) world.getTileEntity(x, y, z)).getStackInSlot(i));
                }
            }

            ItemStack bagOCash = new ItemStack(MLItems.bagOCash);
            ItemWallet.setHoldings(bagOCash, cashStacks);
            InventoryUtils.dropItem(bagOCash, world, vec3);
        }
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon(Minelife.MOD_ID + ":cash_100");
    }


    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return icon;
    }
}
