package com.minelife.permission;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minelife.AbstractMod;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ModPermission extends AbstractMod {

    private static MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) return;

        try {
            config = new MLConfig("permissions");
            setupConfig();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getPermissions(UUID playerID) {
        List<String> groupPerms = Lists.newArrayList();
        getGroups(playerID).forEach(g -> groupPerms.addAll(getPermissions(g)));
        List<String> playerPerms = Lists.newArrayList();
        if(config.contains("users." + playerID.toString() + ".permissions"))
            playerPerms.addAll(config.getStringList("users." + playerID.toString() + ".permissions"));

        playerPerms.forEach(p -> {
            if (p.startsWith("-")) {
                groupPerms.remove(p.substring(1));
            }
        });

        groupPerms.addAll(playerPerms);
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

        Set<String> groups = Sets.newTreeSet();
        groups.addAll(inheritedGroups);

        for (String g : groups) {
            if(config.contains("groups." + g + ".permissions"))
                permissions.addAll(config.getStringList("groups." + g + ".permissions"));
        }

        toRemove.clear();
        permissions.forEach(p -> {
            if(p.startsWith("-")) {
                toRemove.add(p.substring(1));
                toRemove.add(p);
            }
        });

        permissions.removeAll(toRemove);

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
        if(!config.contains("users." + playerID.toString() + ".groups")) return Arrays.asList(getDefaultGroup());
        return config.getStringList("users." + playerID.toString() + ".groups");
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

    public static String getPrefix(String group) {
        return config.getString("groups." + group + ".prefix", null);
    }

    public static String getSuffix(String group) {
        return config.getString("groups." + group + ".suffix", null);
    }

    public static String getPrefix(UUID playerID) {
        return config.getString("users." + playerID.toString() + ".prefix", null);
    }

    public static String getSuffix(UUID playerID) {
        return config.getString("users." + playerID.toString() + ".suffix", null);
    }

    /**
     *
     *
     *
     *
     */
    private void setupConfig() {
        config.addDefault("groups.default.default", true);
        config.addDefault("groups.moderator.inheritance", Arrays.asList("default"));
        config.addDefault("groups.moderator.prefix", EnumChatFormatting.DARK_BLUE.toString() + "[Moderator]" + EnumChatFormatting.RESET.toString());
        config.addDefault("groups.admin.inheritance", Arrays.asList("moderator"));
        config.addDefault("groups.admin.prefix", EnumChatFormatting.DARK_RED.toString() + "[Admin]" + EnumChatFormatting.RESET.toString());
        config.addDefault("users", Lists.newArrayList());
        config.save();
    }

}
