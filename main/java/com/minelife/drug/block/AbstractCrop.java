package com.minelife.drug.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public abstract class AbstractCrop extends BlockBush implements IGrowable {

    @SideOnly(Side.CLIENT)
    protected IIcon[] iIcon;

    public AbstractCrop()
    {
        // Basic block setup
        setTickRandomly(true);
        float f = 0.5F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        setCreativeTab(null);
        setHardness(0.0F);
        setStepSound(soundTypeGrass);
        disableStats();
    }

    public abstract int chance_for_growth();
    public abstract int[] bonemeal_growth_range();
    public abstract int max_growth_stage();
    public abstract void register_icons(IIcon[] icon_array, IIconRegister icon_register);

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister parIIconRegister)
    {
        iIcon = new IIcon[max_growth_stage()+1];
        register_icons(iIcon, parIIconRegister);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    @Override
    public void updateTick(World parWorld, int parX, int parY, int parZ, Random parRand)
    {
        super.updateTick(parWorld, parX, parY, parZ, parRand);
        int growStage = parWorld.getBlockMetadata(parX, parY, parZ) + 1;

        if (growStage > max_growth_stage())
        {
            growStage = max_growth_stage();
        }

        parWorld.setBlockMetadataWithNotify(parX, parY, parZ, growStage, 2);
    }

    @Override
    protected boolean canPlaceBlockOn(Block block)
    {
        return block == Blocks.farmland;
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random parRand, int parFortune)
    {
        return Item.getItemFromBlock(this);
    }

    public void incrementGrowStage(World parWorld, Random parRand, int parX, int parY, int parZ)
    {
        int growStage = parWorld.getBlockMetadata(parX, parY, parZ) +
                (parRand.nextInt(100) < chance_for_growth() ? MathHelper.getRandomIntegerInRange(parRand, bonemeal_growth_range()[0], bonemeal_growth_range()[1]) : 0);

        if (growStage > max_growth_stage())
        {
            growStage = max_growth_stage();
        }

        parWorld.setBlockMetadataWithNotify(parX, parY, parZ, growStage, 2);
    }


    @Override
    // checks if finished growing
    public boolean func_149851_a(World p_149851_1_, int p_149851_2_, int p_149851_3_, int p_149851_4_, boolean p_149851_5_)
    {
        return p_149851_1_.getBlockMetadata(p_149851_2_, p_149851_3_, p_149851_4_) != max_growth_stage();
    }

    @Override
    public boolean func_149852_a(World p_149852_1_, Random p_149852_2_, int p_149852_3_, int p_149852_4_, int p_149852_5_)
    {
        return true;
    }

    @Override
    // when bone meal is applied
    public void func_149853_b(World p_149853_1_, Random p_149853_2_, int p_149853_3_, int p_149853_4_, int p_149853_5_)
    {
        incrementGrowStage(p_149853_1_, p_149853_2_, p_149853_3_, p_149853_4_, p_149853_5_);
    }

    @Override
    public int getRenderType()
    {
        return 1; // Cross like flowers
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int parSide, int parGrowthStage)
    {
        if(parGrowthStage >= max_growth_stage()) return iIcon[max_growth_stage() - 1];
        return iIcon[parGrowthStage];
    }

    public int get_growth_stage(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    public void set_growth_stage(World world, int x, int y, int z, int growth_stage) {
        if (growth_stage > max_growth_stage())
        {
            growth_stage = max_growth_stage();
        }

        world.setBlockMetadataWithNotify(x, y, z, growth_stage, 2);
    }

}