package com.minelife.economy.block;

import com.minelife.Minelife;
import com.minelife.economy.GuiHandler;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.tileentity.TileEntityCash;
import com.minelife.util.client.MLParticleDigging;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.sql.SQLException;

public class BlockCash extends BlockContainer {

    public BlockCash() {
        super(Material.CARPET);
        setRegistryName("cash");
        setUnlocalizedName(Minelife.MOD_ID + ":cash");
        setCreativeTab(CreativeTabs.MISC);
        setHardness(1F);
        setSoundType(SoundType.CLOTH);
    }

    @SideOnly(Side.SERVER)
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        try {
            ModEconomy.getDatabase().query("INSERT INTO cashBlocks (dimension, x, y, z) VALUES ('" + worldIn.provider.getDimension() + "', " +
                    "'" + pos.getX() + "', '" + pos.getY() + "', '" + pos.getZ() + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote && playerIn.isSneaking())
            playerIn.openGui(Minelife.getInstance(), GuiHandler.CASH_BLOCK_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCash();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        MLParticleDigging.addBreakEffect(worldObj, target.getBlockPos(), target.sideHit, manager, "minelife:textures/item/cash_1.png");
        return true;
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        MLParticleDigging.addDestroyEffect(world, pos, manager, "minelife:textures/item/cash_1.png");
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntityCash tile = (TileEntityCash) worldIn.getTileEntity(pos);
        if(tile != null)
            InventoryHelper.dropInventoryItems(worldIn, pos, tile.getInventory());

        if(!worldIn.isRemote) {
            try {
                ModEconomy.getDatabase().query("DELETE FROM cashBlocks WHERE dimension='" + worldIn.provider.getDimension() + "' " +
                        "AND x='" + pos.getX() + "' AND y='" + pos.getY() + "' AND z='" + pos.getZ() + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
}
