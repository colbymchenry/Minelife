package com.minelife.tdm;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class Arena implements Comparable<Arena> {

    public static Set<Arena> ARENAS = Sets.newTreeSet();

    private int dimension;
    private BlockPos team1Spawn, team2Spawn, exitSpawn;
    private String name;
    private MLConfig config;

    public Arena(String name, int dimension, BlockPos team1Spawn, BlockPos team2Spawn, BlockPos exitSpawn) throws Exception {
        this.name = name.toLowerCase();
        this.dimension = dimension;
        this.team1Spawn = team1Spawn;
        this.team2Spawn = team2Spawn;
        this.exitSpawn = exitSpawn;
        File file = new File(Minelife.getDirectory(), "arenas/" + name.toLowerCase());
        if(file.exists()) throw new Exception("Arena with that name already exists.");
        config = new MLConfig(new File(Minelife.getDirectory(), "arenas"), name.toLowerCase());
        config.set("dimension", dimension);
        config.set("team1spawn.x", team1Spawn.getX());
        config.set("team1spawn.y", team1Spawn.getY());
        config.set("team1spawn.z", team1Spawn.getZ());
        config.set("team2spawn.x", team2Spawn.getX());
        config.set("team2spawn.y", team2Spawn.getY());
        config.set("team2spawn.z", team2Spawn.getZ());
        config.set("exitspawn.x", exitSpawn.getX());
        config.set("exitspawn.y", exitSpawn.getY());
        config.set("exitspawn.z", exitSpawn.getZ());
        config.save();
        ARENAS.add(this);
    }

    public Arena(String name) throws IOException, InvalidConfigurationException {
        config = new MLConfig(new File(Minelife.getDirectory(), "arenas"), name.toLowerCase());
        dimension = config.getInt("dimension");
        team1Spawn = new BlockPos(config.getInt("team1spawn.x"), config.getInt("team1spawn.y"), config.getInt("team1spawn.z"));
        team2Spawn = new BlockPos(config.getInt("team2spawn.x"), config.getInt("team2spawn.y"), config.getInt("team2spawn.z"));
        exitSpawn = new BlockPos(config.getInt("exitspawn.x"), config.getInt("exitspawn.y"), config.getInt("exitspawn.z"));
        this.name = name.toLowerCase();
        ARENAS.add(this);
    }

    public int getDimension() {
        return dimension;
    }

    public BlockPos getTeam1Spawn() {
        return team1Spawn;
    }

    public BlockPos getTeam2Spawn() {
        return team2Spawn;
    }

    public BlockPos getExitSpawn() {
        return exitSpawn;
    }

    public void setTeam1Spawn(BlockPos team1Spawn) {
        this.team1Spawn = team1Spawn;
        config.set("team1spawn.x", team1Spawn.getX());
        config.set("team1spawn.y", team1Spawn.getY());
        config.set("team1spawn.z", team1Spawn.getZ());
        config.save();
    }

    public void setTeam2Spawn(BlockPos team2Spawn) {
        this.team2Spawn = team2Spawn;
        config.set("team2spawn.x", team2Spawn.getX());
        config.set("team2spawn.y", team2Spawn.getY());
        config.set("team2spawn.z", team2Spawn.getZ());
        config.save();
    }

    public void setExitSpawn(BlockPos exitSpawn) {
        this.exitSpawn = exitSpawn;
        config.set("exitspawn.x", exitSpawn.getX());
        config.set("exitspawn.y", exitSpawn.getY());
        config.set("exitspawn.z", exitSpawn.getZ());
        config.save();
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Arena o) {
        return o.getName().compareTo(getName());
    }
}
