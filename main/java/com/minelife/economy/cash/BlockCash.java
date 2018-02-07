package com.minelife.economy.cash;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.Lists;
import com.minelife.MLBlocks;
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.sql.SQLException;
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
                    if (oldStackSize == newStackSize) {
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
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        if(world.isRemote) return;

        int l = MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if(world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityCash) {
            TileEntityCash tileEntityCash = (TileEntityCash) world.getTileEntity(x, y, z);
            tileEntityCash.setFacing(l == 0 ? EnumFacing.NORTH : l == 1 ? EnumFacing.EAST : l == 2 ? EnumFacing.SOUTH : EnumFacing.WEST);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_) {
        if(world.isRemote) return;

        Vector3 vec3 = new Vector3(x, y + 0.25, z);
        if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityCash) {
            TileEntityCash tileCash = (TileEntityCash) world.getTileEntity(x, y, z);
            List<ItemStack> cashStacks = Lists.newArrayList();
            for (int i = 0; i < tileCash.getSizeInventory(); i++) {
                if (tileCash.getStackInSlot(i) != null) cashStacks.add(tileCash.getStackInSlot(i));
            }

            if(!cashStacks.isEmpty()) {
                ItemStack bagOCash = new ItemStack(MLItems.bagOCash);
                ItemWallet.setHoldings(bagOCash, cashStacks);
                InventoryUtils.dropItem(bagOCash, world, vec3);
            }
        }

        InventoryUtils.dropItem(new ItemStack(MLBlocks.cash), world, vec3);

        try {
            Minelife.SQLITE.query("DELETE FROM cash_blocks WHERE x='" + x + "' AND y='" + y + "' AND z='" + z + "' AND dimension='" + world.provider.dimensionId + "'");
        } catch (SQLException e) {
            e.printStackTrace();
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
