package com.minelife.jobs;

import com.minelife.Minelife;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

public abstract class NPCHandler {

    protected static final Random r = new Random();
    protected String name;
    protected MLConfig config;
    protected double lvlConst = 1.5;

    protected NPCHandler(String name) {
        this.name = name;
        try {
            config = new MLConfig(new File(Minelife.getDirectory(), "jobs"), "farmer");
            config.addDefault("MaxLevel", 1500);
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
                int lvl = (int) Math.floor(lvlConst * Math.sqrt(result.getInt("xp")));
                return lvl > config.getInt("MaxLevel") ? config.getInt("MaxLevel") : lvl;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public MLConfig getConfig() {
        return config;
    }

    public abstract void onEntityRightClick(EntityPlayer player);

}
