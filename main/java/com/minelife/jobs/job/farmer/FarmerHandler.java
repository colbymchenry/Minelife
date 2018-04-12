package com.minelife.jobs.job.farmer;

import com.minelife.Minelife;
import com.minelife.jobs.ModJobs;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

public class FarmerHandler {

    private static final Random r = new Random();

    public static final double lvlConst = 1.5;
    public static MLConfig config;

    static {
        try {
            config = new MLConfig(new File(Minelife.getDirectory(), "jobs"), "farmer");
            config.addDefault("MaxLevel", 1500);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static boolean isFarmer(EntityPlayerMP player) {
        return getLevel(player) != -1;
    }

    public static int getLevel(EntityPlayerMP player) {
        return getLevel(player.getUniqueID());
    }

    public static int getLevel(UUID playerID) {
        try {
            ResultSet result = ModJobs.getDatabase().query("SELECT * FROM farmer WHERE playerID='" + playerID.toString() + "'");
            if (result.next()) {
                int lvl = (int) Math.floor(lvlConst * Math.sqrt(result.getInt("xp")));
                return lvl > config.getInt("MaxLevel") ? config.getInt("MaxLevel") : lvl;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean replant(EntityPlayerMP player) {
        double chance = getLevel(player) / config.getInt("MaxLevel");
        return r.nextInt(100) < chance;
    }

    public static int getGrowthStage(EntityPlayerMP player) {
        return 5 - (config.getInt("MaxLevel") / getLevel(player));
    }
}