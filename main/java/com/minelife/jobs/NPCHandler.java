package com.minelife.jobs;

import com.minelife.Minelife;
import com.minelife.jobs.job.SellingOption;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public abstract class NPCHandler {

    protected static final Random r = new Random();
    protected String name;
    protected MLConfig config;

    protected NPCHandler(String name) {
        this.name = name;
        try {
            config = new MLConfig(new File(Minelife.getDirectory(), "jobs"), name);
            config.addDefault("MaxLevel", 1500);
            config.save();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public boolean isProfession(EntityPlayerMP player) {
        return getLevel(player) != -1;
    }

    public int getLevel(EntityPlayerMP player) {
        return getLevel(player.getUniqueID());
    }

    public int getLevel(UUID playerID) {
        try {
            ResultSet result = ModJobs.getDatabase().query("SELECT * FROM " + name + " WHERE playerID='" + playerID.toString() + "'");
            if (result.next()) {
                long xp = result.getLong("xp");
                for(int lvl = config.getInt("MaxLevel"); lvl > 0; lvl--) {
                    double xpNeeded = Math.floor(100.0D * (Math.pow(lvl, 2.0D)) - (100.0D * lvl));
                    if(xp >= xpNeeded) return lvl;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long getXP(UUID playerID) {
        try {
            ResultSet result = ModJobs.getDatabase().query("SELECT * FROM " + name + " WHERE playerID='" + playerID.toString() + "'");
            if (result.next()) return result.getLong("xp");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void addXP(UUID playerID, long xp) {
        try {
            long newXP = getXP(playerID) + xp;
            newXP = newXP < 0 ? 0 : newXP;
            ModJobs.getDatabase().query("UPDATE " + name + " SET xp='" + newXP + "' WHERE playerID='" + playerID.toString() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeXP(UUID playerID, long xp) {
        try {
            long newXP = getXP(playerID) - xp;
            newXP = newXP < 0 ? 0 : newXP;
            ModJobs.getDatabase().query("UPDATE " + name + " SET xp='" + newXP + "' WHERE playerID='" + playerID.toString() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MLConfig getConfig() {
        return config;
    }

    public abstract void onEntityRightClick(EntityPlayer player);

    public abstract void joinProfession(EntityPlayer player);

    public abstract List<SellingOption> getSellingOptions();

    public abstract void setupConfig();

}
