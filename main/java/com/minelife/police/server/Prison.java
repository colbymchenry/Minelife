package com.minelife.police.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Prison implements Comparable<Prison> {

    public static Set<Prison> PRISONS = Sets.newTreeSet();

    private MLConfig config;
    private UUID estateID;
    private Map<UUID, Integer> prisonersWithBailMap = Maps.newHashMap();
    private BlockPos dropOffPos;

    public Prison(UUID estateID) throws IOException, InvalidConfigurationException {
        this.estateID = estateID;
        config = new MLConfig(new File(Minelife.getDirectory(), "prisons"), estateID.toString());
        if (config.contains("bail")) {
            for (String bail : config.getStringList("bail")) {
                prisonersWithBailMap.put(UUID.fromString(bail.split(",")[0]), Integer.parseInt(bail.split(",")[1]));
            }
        }

        if(config.contains("dropoff"))
            dropOffPos = new BlockPos(config.getInt("dropoff.x"), config.getInt("dropoff.y"), config.getInt("dropoff.z"));

        PRISONS.add(this);
    }

    public static void initPrisons() throws IOException, InvalidConfigurationException {
        File file = new File(Minelife.getDirectory(), "prisons");
        if(file.listFiles() != null) {
            for (File file1 : Objects.requireNonNull(file.listFiles())) {
                new Prison(UUID.fromString(file1.getName().replace(".yml", "")));
            }
        }
    }

    public BlockPos getDropOffPos() {
        return dropOffPos;
    }

    public void setDropOffPos(BlockPos dropOffPos) {
        this.dropOffPos = dropOffPos;
        config.set("dropoff.x", dropOffPos.getX());
        config.set("dropoff.y", dropOffPos.getY());
        config.set("dropoff.z", dropOffPos.getZ());
        config.save();
    }

    public UUID getEstateID() {
        return estateID;
    }

    public Map<UUID, Integer> getPrisonersWithBailMap() {
        return prisonersWithBailMap;
    }

    public void setPrisonersWithBailMap(Map<UUID, Integer> prisonersWithBailMap) {
        this.prisonersWithBailMap = prisonersWithBailMap;
        List<String> bailList = Lists.newArrayList();
        prisonersWithBailMap.forEach((playerID, bail) -> bailList.add(playerID.toString() + "," + bail));
        config.set("bail", bailList);
        config.save();
    }

    public void addToPrison(UUID playerID, int bail) {
        Map<UUID, Integer> bailMap = Maps.newHashMap();
        bailMap.putAll(this.prisonersWithBailMap);
        bailMap.put(playerID, bail);
        setPrisonersWithBailMap(bailMap);
    }

    @Override
    public int compareTo(Prison o) {
        return o.estateID.compareTo(estateID);
    }

    public static Prison getClosestPrison(BlockPos pos) {
        double distance = Integer.MAX_VALUE;
        Prison closest = null;
        for (Prison prison : PRISONS) {
            Estate estate = ModRealEstate.getEstate(prison.estateID);
            int centerX = estate.getMaximum().getX() - ((estate.getMaximum().getX() - estate.getMinimum().getX()) / 2);
            int centerY = estate.getMaximum().getY() - ((estate.getMaximum().getY() - estate.getMinimum().getY()) / 2);
            int centerZ = estate.getMaximum().getZ() - ((estate.getMaximum().getX() - estate.getMinimum().getZ()) / 2);
            BlockPos center = new BlockPos(centerX, centerY, centerZ);
            double d = center.getDistance(pos.getX(), pos.getY(), pos.getZ());
            if(d < distance) {
                closest = prison;
                distance = d;
            }
        }
        return closest;
    }

    public static Prison getPrison(BlockPos pos) {
        return PRISONS.stream().filter(prison -> {
            if(ModRealEstate.getEstate(prison.estateID) != null) {
                if(ModRealEstate.getEstate(prison.estateID).contains(pos)) {
                    return true;
                }
            }
            return false;
        }).findFirst().orElse(null);
    }
}
