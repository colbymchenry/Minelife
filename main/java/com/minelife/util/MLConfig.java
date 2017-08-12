package com.minelife.util;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.util.configuration.InvalidConfigurationException;
import com.minelife.util.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MLConfig extends YamlConfiguration {

    private final File file;

    public MLConfig(String name) throws IOException, InvalidConfigurationException
    {
        this.file = new File(Minelife.getConfigDirectory(), name + ".yml");
        if(!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            this.file.createNewFile();
        }
        this.load(this.file);
    }

    public void save() {
        try {
            options().copyDefaults(true);
            this.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
