package com.minelife.economy.block;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.client.gui.atm.GuiATMMenu;
import com.minelife.economy.network.PacketOpenATM;
import com.minelife.economy.server.CommandEconomy;
import com.minelife.util.client.MLParticleDigging;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote)
            Minelife.getNetwork().sendTo(new PacketOpenATM(ModEconomy.getBalanceATM(playerIn.getUniqueID())), (EntityPlayerMP) playerIn);
        else
            CommandEconomy.sendMessage(playerIn, "Fetching balance...");
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
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
