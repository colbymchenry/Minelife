package com.minelife.drug.block;

import com.google.common.collect.Lists;
import com.minelife.MLBlocks;
import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.ArrayList;
import java.util.Random;

public class BlockPyrolusiteOre extends Block {

    public BlockPyrolusiteOre()
    {
        super(Material.rock);
        setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston);
        setCreativeTab(ModDrugs.tab_drugs);
        setBlockTextureName(Minelife.MOD_ID + ":pyrolusite_ore");
        setBlockName("pyrolusite_ore");
        GameRegistry.registerWorldGenerator(new Generator(), 5);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> drops = Lists.newArrayList();
        drops.add(new ItemStack(MLItems.pyrolusite, fortune + MathHelper.getRandomIntegerInRange(world.rand, 2, 4)));
        return drops;
    }

    private class Generator implements IWorldGenerator {

        @Override
        public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
        {
            // only over-world
            if (world.provider.dimensionId == 0) {
                this.generate(MLBlocks.pyrolusite_ore, world, random, chunkX, chunkZ, 7, 20, 30, 60, Blocks.stone);
            }
        }

        public void generate(Block block, World world, Random random, int chunk_x, int chunk_z, int max_vein_size, int chance, int min_y, int max_y, Block generate_in)
        {
            int y_range = max_y - min_y;
            WorldGenMinable worldgenminable = new WorldGenMinable(block, max_vein_size, generate_in);
            for (int k1 = 0; k1 < chance; ++k1) {
                int x = random.nextInt(16);
                int y = random.nextInt(y_range) + min_y;
                int z = random.nextInt(16);
                worldgenminable.generate(world, random, chunk_x + x, y, chunk_z + z);
            }
        }
    }

}