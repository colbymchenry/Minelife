package com.minelife.resourcefulness.forest;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.resourcefulness.quarry.Quarry;
import com.minelife.resourcefulness.quarry.QuarryBlock;
import com.minelife.resourcefulness.quarry.QuarryTable;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import com.minelife.util.irds.IRDSObject;
import com.minelife.util.irds.IRDSValue;
import com.minelife.util.irds.RDSNullValue;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.forge.ForgeWorld;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import com.sk89q.worldedit.util.TreeGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class Forest {

    private MLConfig config;
    private UUID id;
    private int dimension, secondsBetweenReset;
    private double density;
    private BlockPos min, max, exit;
    private ForestTable forestTable = new ForestTable(this);

    public Forest(UUID id) throws IOException, InvalidConfigurationException {
        config = new MLConfig(new File(Minelife.getDirectory() + "/resourcefulness/forests"), id.toString());
        this.id = id;
        this.dimension = config.getInt("Dimension");
        this.secondsBetweenReset = config.getInt("SecondsBetweenReset");
        this.min = new BlockPos(config.getInt("min.x"), config.getInt("min.y"), config.getInt("min.z"));
        this.max = new BlockPos(config.getInt("max.x"), config.getInt("max.y"), config.getInt("max.z"));
        this.exit = new BlockPos(config.getInt("exit.x"), config.getInt("exit.y"), config.getInt("exit.z"));
        this.density = config.getDouble("density");
        this.setupDefaultTrees();
    }

    public Forest(int dimension, int secondsBetweenReset, BlockPos min, BlockPos max, BlockPos exit) throws IOException, InvalidConfigurationException {
        this.id = UUID.randomUUID();
        config = new MLConfig(new File(Minelife.getDirectory() + "/resourcefulness/forests"), id.toString());
        setDimension(dimension);
        setSecondsBetweenReset(secondsBetweenReset);
        setDensity(40.0D);
        setMin(min);
        setMax(max);
        setExit(exit);
    }

    public MLConfig getConfig() {
        return config;
    }

    public UUID getID() {
        return id;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
        config.set("density", density);
        config.save();
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

    public List<IRDSValue> getTrees() {
        final List<IRDSValue> blockList = Lists.newArrayList();
        blockList.add(new RDSNullValue(100.0D - config.getDouble("density")));
        config.getStringList("trees").forEach(tree -> blockList.add(ForestTree.fromString(tree)));
        return blockList;
    }

    public void setupDefaultTrees() {
        List<ForestTree> defaultTrees = Lists.newArrayList();
        defaultTrees.add(new ForestTree(TreeGenerator.TreeType.TREE, 35, false, false, true));
        defaultTrees.add(new ForestTree(TreeGenerator.TreeType.ACACIA, 5, true, false, true));
        defaultTrees.add(new ForestTree(TreeGenerator.TreeType.BIRCH, 1, true, false, true));
        defaultTrees.add(new ForestTree(TreeGenerator.TreeType.DARK_OAK, 5, false, false, true));
        defaultTrees.add(new ForestTree(TreeGenerator.TreeType.PINE, 10, false, false, true));
        defaultTrees.add(new ForestTree(TreeGenerator.TreeType.REDWOOD, 2, true, false, true));
        defaultTrees.add(new ForestTree(TreeGenerator.TreeType.JUNGLE, 5, true, false, true));

        List<String> defaultTreeList = Lists.newArrayList();
        defaultTrees.forEach(loot -> defaultTreeList.add(loot.toString()));
        config.addDefault("trees", defaultTreeList);
        config.save();
    }

    public void generate() throws MaxChangedBlocksException {
        com.sk89q.worldedit.world.World world = ForgeWorldEdit.inst.getWorld(getWorld());
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, forestTable.getCount());
        List<IRDSObject> trees = forestTable.getResult();


        for (int x = getMin().getX(); x < getMax().getX(); x++) {
            for (int y = getMin().getY() + 1; y < getMax().getY(); y++) {
                for (int z = getMin().getZ(); z < getMax().getZ(); z++) {
                   getWorld().setBlockToAir(new BlockPos(x, y, z));
                   getWorld().setBlockState(new BlockPos(x, min.getY(), z), Blocks.GRASS.getDefaultState());
                }
            }
        }

        int i = 0;
        for (int x = getMin().getX(); x < getMax().getX(); x++) {
            for (int y = getMin().getY(); y < getMax().getY(); y++) {
                for (int z = getMin().getZ(); z < getMax().getZ(); z++) {

                    i = i >= trees.size() ? trees.size() - 1 : i;
                    if(getWorld().getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.GRASS) {
                        if(!(trees.get(i) instanceof RDSNullValue)) {
                            world.generateTree(((ForestTree) trees.get(i)).getValue(), editSession, new Vector(x, y, z));
                        }
                        i++;
                    }
                }
            }
        }
    }

}
