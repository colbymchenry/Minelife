package com.minelife.drugs.block;

import buildcraft.lib.block.BlockBCTile_Neptune;
import buildcraft.lib.tile.TileBC_Neptune;
import com.minelife.Minelife;
import com.minelife.drugs.DrugsGuiHandler;
import com.minelife.drugs.tileentity.TileEntityLeafMulcher;
import com.minelife.drugs.tileentity.TileEntityVacuum;
import com.minelife.util.client.MLParticleDigging;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockLeafMulcher extends BlockBCTile_Neptune {

    public BlockLeafMulcher() {
        super(Material.IRON, null);
        setRegistryName(Minelife.MOD_ID, "leaf_mulcher");
        setUnlocalizedName(Minelife.MOD_ID + ":leaf_mulcher");
        setHardness(3);
        setResistance(15);
        setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockToAir(pos);
        // TODO: Leaf Mulcher and CementMixer can't see on placement for other players, need right update flag
        worldIn.setBlockState(pos.add(0, 1, 0), this.getDefaultState(), 2);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.add(0, 1, 0)).getBlock() == Blocks.AIR;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(worldIn.isRemote) return false;
        playerIn.openGui(Minelife.getInstance(), DrugsGuiHandler.LEAF_MULCHER_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return false;
    }

    @Nullable
    @Override
    public TileBC_Neptune createTileEntity(World world, IBlockState iBlockState) {
        return new TileEntityLeafMulcher();
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        MLParticleDigging.addBreakEffect(worldObj, target.getBlockPos(), target.sideHit, manager, "minelife:textures/block/leaf_mulcher.png");
        return true;
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        MLParticleDigging.addDestroyEffect(world, pos, manager, "minelife:textures/block/leaf_mulcher.png");
        return true;
    }
}
