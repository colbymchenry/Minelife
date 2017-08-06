package com.minelife.drug.block;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

public class BlockLimestone extends Block {

    public BlockLimestone() {
        super(Material.rock);
        setCreativeTab(ModDrugs.tab_drugs);
        setBlockName("limestone");
        setBlockTextureName(Minelife.MOD_ID + ":limestone");
        GameRegistry.registerWorldGenerator(new Generator(), 0);
    }

    private class Generator implements IWorldGenerator {

        @Override
        public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
        {
            // only over-world
            if (world.provider.dimensionId == 0) {
                this.generate(Minelife.blocks.limestone, world, random, chunkX, chunkZ, 20, 50, 20, 60, Blocks.stone);
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
