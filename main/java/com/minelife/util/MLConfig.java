package com.minelife.util;

import com.minelife.Minelife;
import com.minelife.util.configuration.InvalidConfigurationException;
import com.minelife.util.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MLConfig extends YamlConfiguration {

    private final File file;

    public MLConfig(String name) throws IOException, InvalidConfigurationException
    {
        name = name.contains(".yml") ? name.replaceAll(".yml", "") : name;
        this.file = new File(Minelife.getConfigDirectory(), name + ".yml");
        if(!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            this.file.createNewFile();
        }
        this.load(this.file);
    }

    public MLConfig(File directory, String name) throws IOException, InvalidConfigurationException {
        name = name.contains(".yml") ? name.replaceAll(".yml", "") : name;
        this.file = new File(directory, name + ".yml");
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

    public void set(String path, UUID uuid) {
        this.set(path, uuid.toString());
    }

    public void set(String path, Vector vector) {
        this.set(path, vector.serialize());
    }

    public File getFile() { return file; }

}
