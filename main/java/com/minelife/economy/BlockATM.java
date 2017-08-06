package com.minelife.economy;

import com.minelife.Minelife;
import com.minelife.economy.packet.PacketOpenATM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockATM extends BlockContainer {

    private IIcon icon;

    public BlockATM() {
        super(Material.iron);

        this.setBlockName("atm");
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setBlockBounds(0, 0, 0, 1, 2, 1);
    }


    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) == null) return null;

        TileEntityATM tileEntityATM = (TileEntityATM) world.getTileEntity(x, y, z);

        if (tileEntityATM.getFacing() == EnumFacing.NORTH)
            return AxisAlignedBB.getBoundingBox(x + 0, y + 0, z + 0.5f, x + 1, y + 2f, z + 1);
        else if (tileEntityATM.getFacing() == EnumFacing.EAST)
            return AxisAlignedBB.getBoundingBox(x + 0, y + 0, z + 0, x + 0.5f, y + 2f, z + 1);
        else if (tileEntityATM.getFacing() == EnumFacing.SOUTH)
            return AxisAlignedBB.getBoundingBox(x + 0, y + 0, z + 0, x + 1, y + 2f, z + 0.5f);
        else
            return AxisAlignedBB.getBoundingBox(x + 0.5f, y + 0, z + 0, x + 1, y + 2f, z + 1);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) == null) return;

        TileEntityATM tileEntityATM = (TileEntityATM) world.getTileEntity(x, y, z);

        if (tileEntityATM.getFacing() == EnumFacing.NORTH)
            this.setBlockBounds(0, 0, 0.5f, 1, 2.2f, 1);
        else if (tileEntityATM.getFacing() == EnumFacing.EAST)
            this.setBlockBounds(0, 0, 0, 0.5f, 2.2f, 1);
        else if (tileEntityATM.getFacing() == EnumFacing.SOUTH)
            this.setBlockBounds(0, 0, 0, 1, 2.2f, 0.5f);
        else
            this.setBlockBounds(0.5f, 0, 0, 1, 2.2f, 1);
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }


    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack hand) {
        int l = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        TileEntityATM tileEntityATM = (TileEntityATM) world.getTileEntity(x, y, z);
        tileEntityATM.setFacing(l == 0 ? EnumFacing.NORTH : l == 1 ? EnumFacing.EAST : l == 2 ? EnumFacing.SOUTH : EnumFacing.WEST);

        world.setBlock(x, y + 1, z, Minelife.blocks.atm_top);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        try {
            Minelife.NETWORK.sendTo(new PacketOpenATM(ModEconomy.getPin(player.getUniqueID()).isEmpty()), (EntityPlayerMP) player);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int side) {
        if (world.getBlock(x, y + 1, z) == Minelife.blocks.atm_top)
            world.setBlockToAir(x, y + 1, z);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.isAirBlock(x, y + 1, z);
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon(Minelife.MOD_ID + ":atm");
    }

    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return icon;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new TileEntityATM();
    }

}