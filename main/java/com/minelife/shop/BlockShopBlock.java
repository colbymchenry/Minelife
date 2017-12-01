package com.minelife.shop;

import buildcraft.core.lib.block.BlockBuildCraft;
import com.minelife.Minelife;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockShopBlock extends BlockBuildCraft {

    private IIcon icon;

    public BlockShopBlock() {
        super(Material.iron, CreativeTabs.tabRedstone);
        setBlockName("shopBlock");
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityShopBlock();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack hand) {
        int l = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        TileEntityShopBlock tileEntityATM = (TileEntityShopBlock) world.getTileEntity(x, y, z);
        tileEntityATM.setFacing(l == 0 ? EnumFacing.NORTH : l == 1 ? EnumFacing.EAST : l == 2 ? EnumFacing.SOUTH : EnumFacing.WEST);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        try {
//            Minelife.NETWORK.sendTo(new PacketOpenATM(ModEconomy.getPin(player.getUniqueID()).isEmpty()), (EntityPlayerMP) player);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon(Minelife.MOD_ID + ":shop_block");
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
