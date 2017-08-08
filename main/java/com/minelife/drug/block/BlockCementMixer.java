package com.minelife.drug.block;

import buildcraft.core.lib.block.BlockBuildCraft;
import com.minelife.MLBlocks;
import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import com.minelife.drug.tileentity.TileEntityCementMixer;
import com.minelife.drug.tileentity.TileEntityLeafMulcher;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCementMixer extends BlockBuildCraft {

    private IIcon icon;

    public BlockCementMixer()
    {
        super(Material.iron);
        setCreativeTab(ModDrugs.tab_drugs);
        GameRegistry.registerTileEntity(TileEntityCementMixer.class, "cement_mixer");
        setBlockName("cement_mixer");
        setBlockBounds(0, 0, 0, 1, 0.85f, 1);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack hand) {
        world.setBlock(x, y + 1, z, this);
        world.setBlock(x, y, z, Blocks.air);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return world.getBlock(x, y + 1, z) == Blocks.air;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntityCementMixer tile = (TileEntityCementMixer) world.getTileEntity(x, y, z);
        tile.onBlockActivated(player);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityCementMixer();
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon(Minelife.MOD_ID + ":cement_mixer");
    }

    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return icon;
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

}