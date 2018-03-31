package com.minelife.drugs.block;

import com.minelife.Minelife;
import com.minelife.drugs.ModDrugs;
import com.minelife.guns.ModGuns;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockSulfurOre extends Block {

    public BlockSulfurOre() {
        super(Material.ROCK);
        setRegistryName(Minelife.MOD_ID, "sulfur_ore");
        setUnlocalizedName(Minelife.MOD_ID + ":sulfur_ore");
        setHardness(3);
        setResistance(15);
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(CreativeTabs.MISC);
    }

    @SideOnly(Side.CLIENT)
    public void registerModel(ItemModelMesher mesher) {
        Item item = Item.getItemFromBlock(this);
        ModelResourceLocation model = new ModelResourceLocation(Minelife.MOD_ID + ":sulfur_ore", "inventory");
        ModelLoader.registerItemVariants(item, model);
        mesher.register(item, 0, model);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ModDrugs.itemSulfur;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune > 0) {
            int i = random.nextInt(fortune + 2) - 1;
            if (i < 0) {
                i = 0;
            }
            return this.quantityDropped(random) * (i + 1) + 4;
        } else {
            return this.quantityDropped(random) + 4;
        }
    }

    public static class Generator implements IWorldGenerator {

        private WorldGenerator worldGenerator;

        public Generator() {
            worldGenerator = new WorldGenMinable(ModDrugs.blockSulfurOre.getDefaultState(), 7);
        }

        @Override
        public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
            if(world.provider.getDimension() != 0) return;

            runGenerator(worldGenerator, world, random, chunkX, chunkZ, 20, 0, 64);
        }

        private void runGenerator(WorldGenerator gen, World world, Random rand, int chunkX, int chunkZ, int chance, int minHeight, int maxHeight) {

            if(minHeight > maxHeight || minHeight < 0 || maxHeight > 256) throw new IllegalArgumentException("Ore Generated Out of Bounds");
            int heighDiff = maxHeight - minHeight + 1;

            for(int i = 0; i < chance; i++) {
                int x = chunkX * 16 + rand.nextInt(16);
                int y = minHeight + rand.nextInt(heighDiff);
                int z = chunkZ * 16 + rand.nextInt(16);

                gen.generate(world, rand, new BlockPos(x, y, z));
            }
        }
    }

}
