package com.minelife.guns.turret;

import blusunrize.immersiveengineering.common.IEContent;
import codechicken.lib.model.ModelRegistryHelper;
import com.minelife.Minelife;
import com.minelife.guns.GuiHandler;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGun;
import com.minelife.util.client.MLParticleDigging;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static com.minelife.guns.turret.BlockTurret.Type.*;

public class BlockTurret extends BlockContainer {

    private boolean bottom;

    public BlockTurret(boolean bottom) {
        super(Material.ROCK);
        this.bottom = bottom;
        setUnlocalizedName(Minelife.MOD_ID + ":turret_" + bottom);
        setRegistryName(Minelife.MOD_ID, "turret_" + bottom);
        setHardness(3);
        setResistance(15);
        setLightLevel(1);
        if(bottom) setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return true;

        TileEntityTurret tileEntityTurret = (TileEntityTurret) (bottom ? worldIn.getTileEntity(pos) : worldIn.getTileEntity(pos.add(0, -1, 0)));
        if (tileEntityTurret != null && tileEntityTurret.hasPermissionToModifySettings((EntityPlayerMP) playerIn)) {
            playerIn.openGui(Minelife.getInstance(), GuiHandler.TURRET, worldIn, tileEntityTurret.getPos().getX(),
                    tileEntityTurret.getPos().getY(), tileEntityTurret.getPos().getZ());
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!bottom) return;
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

    public void registerRecipe() {
        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":turret");
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this),
                "MAM",
                "MCM",
                "YJW",
                'A', Ingredient.fromStacks(new ItemStack(IEContent.itemToolUpgrades, 1, 8)),
                'Y', Ingredient.fromStacks(new ItemStack(IEContent.itemToolUpgrades, 1, 5)),
                'J', Ingredient.fromStacks(new ItemStack(IEContent.blockWoodenDevice0, 1, 6)),
                'W', Ingredient.fromStacks(new ItemStack(IEContent.blockMetalDecoration0, 1, 3)),
                'C', Ingredient.fromStacks(new ItemStack(IEContent.itemMaterial, 1, 27)),
                'M', Ingredient.fromStacks(new ItemStack(IEContent.itemMetal, 1, 38)));
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        for (Type type : Type.values()) {
            ModelResourceLocation model = new ModelResourceLocation("minelife:turret_" + type.name().toLowerCase(), "inventory");
            ModelLoader.setCustomModelResourceLocation(ModGuns.itemTurret, type.ordinal(), model);
            ModelRegistryHelper.register(model, new ItemRenderTurret(() -> model));
        }
    }

    public enum Type {
        FULL, STAND, HEAD, STAND_EMPTY, HEAD_EMPTY
    }
}
