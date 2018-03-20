package com.minelife.economy.block;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.util.client.MLParticleDigging;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockATMTop extends Block {

    private static AxisAlignedBB BOUNDS = new AxisAlignedBB(0, -1, 0, 1, 1, 1);

    public BlockATMTop() {
        super(Material.IRON);
        setRegistryName("atmTop");
        setUnlocalizedName(Minelife.MOD_ID + ":atmTop");
        setCreativeTab(CreativeTabs.MISC);
        setHardness(10F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        if (worldIn.getBlockState(pos.add(0, -1, 0)).getBlock() == ModEconomy.blockATMBottom)
            worldIn.setBlockToAir(pos.add(0, -1, 0));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDS;
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
        MLParticleDigging.addBreakEffect(worldObj, target.getBlockPos(), target.sideHit, manager, "minelife:textures/block/atm.png");
        return true;
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        MLParticleDigging.addDestroyEffect(world, pos, manager, "minelife:textures/block/atm.png");
        return true;
    }

}
