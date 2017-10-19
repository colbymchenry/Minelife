package com.minelife.permission;

import com.google.common.collect.Lists;
import com.minelife.AbstractMod;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class ModPermission extends AbstractMod {

    private static MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) return;

        try {
            config = new MLConfig("permissions");
            if (config.contains("groups")) {
                getPermissions("Admin");
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getPermissions(UUID playerID) {
        List<String> groupPerms = getPermissions(getGroup(playerID));
        List<String> playerPerms
        return groupPerms;
    }

    public static List<String> getPermissions(String group) {
        List<String> permissions = Lists.newArrayList();
        if (!config.contains("groups." + group)) return permissions;

        List<String> inheritedGroups = Lists.newArrayList();
        getInheritedGroups(group, inheritedGroups);
        List<String> toRemove = Lists.newArrayList();
        inheritedGroups.forEach(g -> {
            if(g.startsWith("-")) toRemove.add(g);
        });
        inheritedGroups.removeAll(toRemove);
        // TODO: Go through permissions and add them and remove the ones that are -perm
        return permissions;
    }

    public static List<String> getInheritedGroups(String group, List<String> result) {
        if (config.contains("groups." + group + ".inheritance")) {
            result.addAll(config.getStringList("groups." + group + ".inheritance"));
            for (String s : config.getStringList("groups." + group + ".inheritance")) getInheritedGroups(s, result);
        }
        return config.contains("groups." + group + ".inheritance") ? config.getStringList("groups." + group + ".inheritance") : Lists.newArrayList();
    }

    public static List<String> getGroups(UUID playerID) {
        // TODO
    }

    public static String getDefaultGroup() {
        for (String group : config.getConfigurationSection("groups").getKeys(false)) {
            if (config.contains("groups." + group + ".default")) {
                if (config.getBoolean("groups." + group + ".default")) return group;
            }
        }
        return null;
    }

    public static boolean hasPermission(UUID playerID, String permission) {
        return getPermissions(playerID).contains(permission);
    }

}
