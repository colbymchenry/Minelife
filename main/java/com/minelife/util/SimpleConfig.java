package com.minelife.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class SimpleConfig {

    private File file;

    public SimpleConfig(File file) throws Exception
    {
        this.file = file;
        if (this.file.getParentFile().exists() && this.file.getParentFile().isDirectory()) {
            this.createConfigFile();
        } else {
            if (this.file.getParentFile().mkdirs()) {
                this.createConfigFile();
            } else {
                Minelife.getLogger().log(Level.SEVERE, "Config: " + file.getName() + " failed to create directories.");
            }
        }
    }

    private void createConfigFile() throws IOException
    {
        if (!this.file.exists()) {
            if (this.file.createNewFile()) {
                Minelife.getLogger().log(Level.INFO, "Config: " + file.getName() + " created!");
            } else {
                Minelife.getLogger().log(Level.SEVERE, "Config: " + file.getName() + " failed to create file.");
            }
        }
    }

    public Map<String, Object> getOptions()
    {
        Map<String, Object> options = Maps.newHashMap();

        try {
            List<String> lines = Files.readAllLines(Paths.get(this.file.getAbsolutePath()));
            lines.forEach(line -> options.put(line.split("=")[0], line.replaceFirst(line.split("=")[0] + "=", "")));
        } catch (IOException e) {
            e.printStackTrace();
            Minelife.getLogger().log(Level.SEVERE, "", e);
        }

        return options;
    }

    public void setOptions(Map<String, Object> options)
    {
        List<String> lines = Lists.newArrayList();

        options.forEach((k, v) -> lines.add(k + "=" + v.toString()));

        try {
            Files.write(Paths.get(this.file.getAbsolutePath()), lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            Minelife.getLogger().log(Level.SEVERE, "", e);
        }
    }

    public void setDefaults(Map<String, Object> defaults)
    {
        Map<String, Object> options = getOptions();
        List<String> lines = Lists.newArrayList();

        for(String key : defaults.keySet()) {
            if(!options.containsKey(key)) options.put(key, defaults.get(key));
        }

        options.forEach((k, v) -> lines.add(k + "=" + v.toString()));

        try {
            Files.write(Paths.get(this.file.getAbsolutePath()), lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            Minelife.getLogger().log(Level.SEVERE, "", e);
        }
    }

}
