package com.minelife.guns.turret;

import com.minelife.Minelife;
import com.minelife.guns.GuiHandler;
import com.minelife.guns.ModGuns;
import com.minelife.util.client.MLParticleDigging;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockTurret extends BlockContainer {

    private boolean bottom;

    public BlockTurret(boolean bottom) {
        super(Material.ROCK);
        this.bottom = bottom;
        setUnlocalizedName(Minelife.MOD_ID + ":turret_" + bottom);
        setRegistryName(Minelife.MOD_ID, "turret_" + bottom);
        setCreativeTab(CreativeTabs.MISC);
        setHardness(3);
        setResistance(15);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(worldIn.isRemote) return true;

        TileEntityTurret tileEntityTurret = (TileEntityTurret) (bottom ? worldIn.getTileEntity(pos) : worldIn.getTileEntity(pos.add(0, -1, 0)));
        if(tileEntityTurret != null) {
            playerIn.openGui(Minelife.getInstance(), GuiHandler.TURRET, worldIn, tileEntityTurret.getPos().getX(),
                    tileEntityTurret.getPos().getY(), tileEntityTurret.getPos().getZ());
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if(!bottom) return;
        int l = MathHelper.floor((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        TileEntityTurret tileTurret = (TileEntityTurret) worldIn.getTileEntity(pos);
        tileTurret.setDirection(l == 0 ? EnumFacing.NORTH : l == 1 ? EnumFacing.EAST : l == 2 ? EnumFacing.SOUTH : EnumFacing.WEST);
        worldIn.setBlockState(pos.add(0, 1, 0), ModGuns.blockTurretTop.getDefaultState());
        tileTurret.setOwner(placer.getUniqueID());
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        if (bottom) {
            worldIn.setBlockToAir(pos.add(0, 1, 0));
            worldIn.removeTileEntity(pos.add(0, 1, 0));
        } else {
            worldIn.setBlockToAir(pos.add(0, -1, 0));
            worldIn.removeTileEntity(pos.add(0, -1, 0));
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return true;
//        return worldIn.getBlockState(pos.add(0, 1, 0)).getBlock() != Blocks.AIR;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return bottom ? new TileEntityTurret() : null;
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
        MLParticleDigging.addBreakEffect(worldObj, target.getBlockPos(), target.sideHit, manager, "minelife:textures/block/turret_body_off.png");
        return true;
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        MLParticleDigging.addDestroyEffect(world, pos, manager, "minelife:textures/block/turret_body_off.png");
        return true;
    }
}
