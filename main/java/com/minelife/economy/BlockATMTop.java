package com.minelife.economy;

import com.minelife.MLBlocks;
import com.minelife.Minelife;
import com.minelife.economy.packet.PacketUnlockATM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockATMTop extends Block {

    private IIcon icon;

    public BlockATMTop() {
        super(Material.iron);
        this.setBlockName("atm_top");
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        if (world.getTileEntity(x, y - 1, z) == null) return;

        TileEntityATM tileEntityATM = (TileEntityATM) world.getTileEntity(x, y - 1, z);

        if (tileEntityATM.getFacing() == EnumFacing.NORTH)
            this.setBlockBounds(0, -1, 0.5f, 1, 1.2f, 1);
        else if (tileEntityATM.getFacing() == EnumFacing.EAST)
            this.setBlockBounds(0, -1, 0, 0.5f, 1.2f, 1);
        else if (tileEntityATM.getFacing() == EnumFacing.SOUTH)
            this.setBlockBounds(0, -1, 0, 1, 1.2f, 0.5f);
        else
            this.setBlockBounds(0.5f, -1, 0, 1, 1.2f, 1);
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        try {
            Minelife.NETWORK.sendTo(new PacketUnlockATM(), (EntityPlayerMP) player);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int side) {
        if (world.getBlock(x, y - 1, z) == MLBlocks.atm)
            world.setBlockToAir(x, y - 1, z);
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

}
