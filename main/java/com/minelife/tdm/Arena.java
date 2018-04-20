package com.minelife.tdm;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.util.math.BlockPos;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Arena implements Comparable<Arena> {

    public static Set<Arena> ARENAS = Sets.newTreeSet();

    private Estate estate;
    private BlockPos team1Spawn, team2Spawn, exitSpawn, lobbySpawn;
    private String name;
    private MLConfig config;

    static {
        File imageFile = new File(Minelife.getDirectory(), "ArenaImages/dummyArena.png");
        imageFile.getParentFile().mkdirs();
    }

    public Arena(String name, Estate estate, BlockPos team1Spawn, BlockPos team2Spawn, BlockPos exitSpawn, BlockPos lobbySpawn) throws Exception {
        this.name = name.toLowerCase();
        this.estate = estate;
        this.team1Spawn = team1Spawn;
        this.team2Spawn = team2Spawn;
        this.exitSpawn = exitSpawn;
        this.lobbySpawn = lobbySpawn;
        File file = new File(Minelife.getDirectory(), "arenas/" + name.toLowerCase());
        if (file.exists()) throw new Exception("Arena with that name already exists.");
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
        config.set("lobbyspawn.x", exitSpawn.getX());
        config.set("lobbyspawn.y", exitSpawn.getY());
        config.set("lobbyspawn.z", exitSpawn.getZ());
        config.save();
        ARENAS.add(this);
    }

    public Arena(String name) throws IOException, InvalidConfigurationException {
        config = new MLConfig(new File(Minelife.getDirectory(), "arenas"), name.toLowerCase());
        estate = ModRealEstate.getEstate(UUID.fromString(config.getString("estate")));
        if (config.contains("team1spawn"))
            team1Spawn = new BlockPos(config.getInt("team1spawn.x"), config.getInt("team1spawn.y"), config.getInt("team1spawn.z"));
        if (config.contains("team2spawn"))
            team2Spawn = new BlockPos(config.getInt("team2spawn.x"), config.getInt("team2spawn.y"), config.getInt("team2spawn.z"));
        if (config.contains("exitspawn"))
            exitSpawn = new BlockPos(config.getInt("exitspawn.x"), config.getInt("exitspawn.y"), config.getInt("exitspawn.z"));
        if (config.contains("lobbyspawn"))
            lobbySpawn = new BlockPos(config.getInt("lobbyspawn.x"), config.getInt("lobbyspawn.y"), config.getInt("lobbyspawn.z"));
        this.name = name.toLowerCase();
        ARENAS.add(this);
    }

    private Arena() {
    }


    public static void initArenas() throws IOException, InvalidConfigurationException {
        File file = new File(Minelife.getDirectory(), "arenas");
        for (File file1 : file.listFiles()) {
            new Arena(file1.getName().replace(".yml", ""));
        }
    }

    public static Arena createArena(String name, Estate estate) throws IOException, InvalidConfigurationException {
        Arena arena = new Arena();
        arena.config = new MLConfig(new File(Minelife.getDirectory(), "arenas"), name.toLowerCase());
        arena.estate = estate;
        arena.name = name;
        arena.config.set("estate", estate.getUniqueID().toString());
        arena.config.save();
        ARENAS.add(arena);
        return arena;
    }

    public static Arena getArena(String name) {
        return ARENAS.stream().filter(arena -> arena.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
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

    public BlockPos getLobbySpawn() {
        return lobbySpawn;
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

    public void setLobbySpawn(BlockPos lobbySpawn) {
        this.lobbySpawn = lobbySpawn;
        config.set("lobbyspawn.x", lobbySpawn.getX());
        config.set("lobbyspawn.y", lobbySpawn.getY());
        config.set("lobbyspawn.z", lobbySpawn.getZ());
        config.save();
    }

    public String getName() {
        return name;
    }

    public String getPixels() {
        File imageFile = new File(Minelife.getDirectory(), "ArenaImages/" + getName() + ".png");
        imageFile.getParentFile().mkdirs();
        if (!imageFile.exists()) return null;

        StringBuilder pixels = new StringBuilder();

        try {
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Raster raster = bufferedImage.getData();
            int numBands = raster.getNumBands();
            int height = raster.getHeight();
            int width = raster.getWidth();

            int[] pixelRow = new int[width * numBands];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    pixels.append(x).append(",").append(y).append(",").append(bufferedImage.getRGB(x, y)).append(";");
                }
            }
        } catch (Exception e) {
            return null;
        }

        return pixels.toString();
    }

    public void delete() {
        Match match = getCurrentMatch();
        if (getCurrentMatch() != null) match.end();
        ARENAS.remove(this);
        new File(Minelife.getDirectory(), "arenas/" + name.toLowerCase() + ".yml").delete();
    }

    public Match getCurrentMatch() {
        return Match.ACTIVE_MATCHES.stream().filter(m -> m.getArena().equals(this)).findFirst().orElse(null);
    }

    @Override
    public int compareTo(Arena o) {
        return o.getName().compareTo(getName());
    }
}
