package com.minelife.drugs.block;

import com.google.common.base.Predicate;
import com.minelife.Minelife;
import com.minelife.drugs.ModDrugs;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class BlockHempCrop extends BlockCrops {

    public BlockHempCrop() {
        setRegistryName(Minelife.MOD_ID, "hemp_crop");
        setUnlocalizedName(Minelife.MOD_ID + ":hemp_crop");
        setTickRandomly(true);
        setCreativeTab(null);
        setHardness(0.0F);
        disableStats();
    }

    // TODO: Still grows in non farmland

    @Override
    protected Item getCrop() {
        return ModDrugs.itemHempBuds;
    }

    @Override
    protected Item getSeed() {
        return ModDrugs.itemHempSeed;
    }

}
