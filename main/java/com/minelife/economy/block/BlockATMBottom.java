package com.minelife.economy.block;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.tileentity.TileEntityATM;
import com.minelife.util.client.MLParticleDigging;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockATMBottom extends BlockContainer {

    private static AxisAlignedBB BOUNDS = new AxisAlignedBB(0, 0, 0, 1, 2, 1);

    public BlockATMBottom() {
        super(Material.IRON);
        setRegistryName("atmBottom");
        setUnlocalizedName(Minelife.MOD_ID + ":atmBottom");
        setCreativeTab(CreativeTabs.MISC);
        setHardness(10F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.add(0, 1, 0)).getBlock() == Blocks.AIR;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (worldIn.isRemote) return;

        worldIn.setBlockState(pos.add(0, 1, 0), ModEconomy.blockATMTop.getDefaultState());
        worldIn.markBlockRangeForRenderUpdate(pos.add(0, 1, 0), pos.add(0, 1, 0));

        int l = MathHelper.floor((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        TileEntityATM tile = (TileEntityATM) worldIn.getTileEntity(pos);
        tile.setFacing(l == 0 ? EnumFacing.NORTH : l == 1 ? EnumFacing.EAST : l == 2 ? EnumFacing.SOUTH : EnumFacing.WEST);
        tile.sendUpdates();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDS;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        if(worldIn.getBlockState(pos.add(0, 1, 0)).getBlock()==ModEconomy.blockATMTop)
            worldIn.setBlockToAir(pos.add(0, 1, 0));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityATM();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        MLParticleDigging.addBreakEffect(worldObj, target.getBlockPos(), target.sideHit, manager, "minelife:textures/block/atm.png");
        return true;
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        MLParticleDigging.addDestroyEffect(world, pos, manager, "minelife:textures/block/atm.png");
        return true;
    }

}
