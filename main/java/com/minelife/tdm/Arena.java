package com.minelife.tdm;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.essentials.Location;
import com.minelife.essentials.TeleportHandler;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.MLConfig;
import com.minelife.util.PlayerHelper;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Arena implements Comparable<Arena> {

    public static Set<Arena> ARENAS = Sets.newTreeSet();

    private Estate estate;
    private BlockPos team1Spawn, team2Spawn, exitSpawn;
    private String name;
    private MLConfig config;

    public Arena(String name, Estate estate, BlockPos team1Spawn, BlockPos team2Spawn, BlockPos exitSpawn) throws Exception {
        this.name = name.toLowerCase();
        this.estate = estate;
        this.team1Spawn = team1Spawn;
        this.team2Spawn = team2Spawn;
        this.exitSpawn = exitSpawn;
        File file = new File(Minelife.getDirectory(), "arenas/" + name.toLowerCase());
        if(file.exists()) throw new Exception("Arena with that name already exists.");
        config = new MLConfig(new File(Minelife.getDirectory(), "arenas"), name.toLowerCase());
        config.set("estate", estate.getUniqueID().toString());
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
        estate = ModRealEstate.getEstate(UUID.fromString(config.getString("estate")));
        team1Spawn = new BlockPos(config.getInt("team1spawn.x"), config.getInt("team1spawn.y"), config.getInt("team1spawn.z"));
        team2Spawn = new BlockPos(config.getInt("team2spawn.x"), config.getInt("team2spawn.y"), config.getInt("team2spawn.z"));
        exitSpawn = new BlockPos(config.getInt("exitspawn.x"), config.getInt("exitspawn.y"), config.getInt("exitspawn.z"));
        this.name = name.toLowerCase();
        ARENAS.add(this);
    }

    private Arena() {}

    public static Arena createArena(String name, Estate estate) throws IOException, InvalidConfigurationException {
        Arena arena = new Arena();
        arena.config = new MLConfig(new File(Minelife.getDirectory(), "arenas"), name.toLowerCase());
        arena.estate = estate;
        arena.config.set("estate", estate.getUniqueID().toString());
        arena.config.save();
        ARENAS.add(arena);
        return arena;
    }

    public Estate getEstate() {
        return estate;
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

    public void delete() {
        Match match = Match.ACTIVE_MATCHES.stream().filter(m -> m.getArena().equals(this)).findFirst().orElse(null);
        match.end();
        ARENAS.remove(this);
        new File(Minelife.getDirectory(), "arenas/" + name.toLowerCase() + ".yml").delete();
    }

    @Override
    public int compareTo(Arena o) {
        return o.getName().compareTo(getName());
    }
}
