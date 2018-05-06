package com.minelife.resourcefulness.quarry;

import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.BlockTypes_MetalsIE;
import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import com.minelife.util.irds.IRDSObject;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class Quarry implements Comparable<Quarry> {

    private MLConfig config;
    private UUID id;
    private int dimension, secondsBetweenReset;
    private BlockPos min, max, exit;
    private QuarryTable quarryTable = new QuarryTable(this);

    public Quarry(UUID id) throws IOException, InvalidConfigurationException {
        config = new MLConfig(new File(Minelife.getDirectory() + "/resourcefulness/quarries"), id.toString());
        this.id = id;
        this.dimension = config.getInt("Dimension");
        this.secondsBetweenReset = config.getInt("SecondsBetweenReset");
        this.min = new BlockPos(config.getInt("min.x"), config.getInt("min.y"), config.getInt("min.z"));
        this.max = new BlockPos(config.getInt("max.x"), config.getInt("max.y"), config.getInt("max.z"));
        this.exit = new BlockPos(config.getInt("exit.x"), config.getInt("exit.y"), config.getInt("exit.z"));
    }

    public Quarry(int dimension, int secondsBetweenReset, BlockPos min, BlockPos max, BlockPos exit) throws IOException, InvalidConfigurationException {
        this.id = UUID.randomUUID();
        config = new MLConfig(new File(Minelife.getDirectory() + "/resourcefulness/quarries"), id.toString());
        setDimension(dimension);
        setSecondsBetweenReset(secondsBetweenReset);
        setMin(min);
        setMax(max);
        setExit(exit);
        setupDefaultOres();
    }

    public MLConfig getConfig() {
        return config;
    }

    public UUID getID() {
        return id;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
        config.set("Dimension", dimension);
        config.save();
    }

    public int getSecondsBetweenReset() {
        return secondsBetweenReset;
    }

    public void setSecondsBetweenReset(int secondsBetweenReset) {
        this.secondsBetweenReset = secondsBetweenReset;
        config.set("SecondsBetweenReset", secondsBetweenReset);
        config.save();
    }

    public BlockPos getMin() {
        return min;
    }

    public void setMin(BlockPos min) {
        this.min = min;
        config.set("min.x", min.getX());
        config.set("min.y", min.getY());
        config.set("min.z", min.getZ());
        config.save();
    }

    public BlockPos getMax() {
        return max;
    }

    public void setMax(BlockPos max) {
        this.max = max;
        config.set("max.x", max.getX());
        config.set("max.y", max.getY());
        config.set("max.z", max.getZ());
        config.save();
    }

    public BlockPos getExit() {
        return exit;
    }

    public void setExit(BlockPos exit) {
        this.exit = exit;
        config.set("exit.x", exit.getX());
        config.set("exit.y", exit.getY());
        config.set("exit.z", exit.getZ());
        config.save();
    }

    @SideOnly(Side.SERVER)
    public World getWorld() {
        return FMLServerHandler.instance().getServer().getWorld(dimension);
    }

    public void setupDefaultOres() {
        List<QuarryBlock> defaultOres = Lists.newArrayList();
        defaultOres.add(new QuarryBlock(Blocks.STONE.getDefaultState(), 85, false, false, true));
        defaultOres.add(new QuarryBlock(Blocks.IRON_ORE.getDefaultState(), 5, true, false, true));
        defaultOres.add(new QuarryBlock(Blocks.COAL_ORE.getDefaultState(), 10, true, false, true));
        defaultOres.add(new QuarryBlock(Blocks.DIAMOND_ORE.getDefaultState(), 0.5, false, false, true));
        defaultOres.add(new QuarryBlock(Blocks.EMERALD_ORE.getDefaultState(), 0.5, false, false, true));
        defaultOres.add(new QuarryBlock(Blocks.REDSTONE_ORE.getDefaultState(), 2, true, false, true));
        defaultOres.add(new QuarryBlock(Blocks.LAPIS_ORE.getDefaultState(), 2, true, false, true));
        defaultOres.add(new QuarryBlock(ModGuns.blockZincOre.getDefaultState(), 5, true, false, true));
        defaultOres.add(new QuarryBlock(IEContent.blockOre.getStateFromMeta(BlockTypes_MetalsIE.COPPER.ordinal()), 5, true, false, true));
        defaultOres.add(new QuarryBlock(IEContent.blockOre.getStateFromMeta(BlockTypes_MetalsIE.ALUMINUM.ordinal()), 2, true, false, true));
        defaultOres.add(new QuarryBlock(IEContent.blockOre.getStateFromMeta(BlockTypes_MetalsIE.NICKEL.ordinal()), 5, true, false, true));
        defaultOres.add(new QuarryBlock(IEContent.blockOre.getStateFromMeta(BlockTypes_MetalsIE.SILVER.ordinal()), 5, true, false, true));
        defaultOres.add(new QuarryBlock(IEContent.blockOre.getStateFromMeta(BlockTypes_MetalsIE.URANIUM.ordinal()), 1, true, false, true));

        List<String> defaultOreList = Lists.newArrayList();
        defaultOres.forEach(loot -> defaultOreList.add(loot.toString()));
        config.addDefault("ores", defaultOreList);
        config.save();
    }

    public List<QuarryBlock> getOres() {
        final List<QuarryBlock> blockList = Lists.newArrayList();
        config.getStringList("ores").forEach(ore -> blockList.add(QuarryBlock.fromString(ore)));
        return blockList;
    }

    public void generate() {
        World world = getWorld();
        List<IRDSObject> ores = quarryTable.getResult();
        int i = 0;
        for (int x = getMin().getX(); x < getMax().getX(); x++) {
            for (int y = getMin().getY(); y < getMax().getY(); y++) {
                for (int z = getMin().getZ(); z < getMax().getZ(); z++) {
                    i = i >= ores.size() ? ores.size() - 1 : i;
                    QuarryBlock quarryBlock = (QuarryBlock) ores.get(i);
                    world.setBlockState(new BlockPos(x, y, z), quarryBlock.getValue());
                    i++;
                }
            }
        }
    }

    @Override
    public int compareTo(Quarry o) {
        return o.id.compareTo(id);
    }

}
