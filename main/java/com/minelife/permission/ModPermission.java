package com.minelife.permission;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minelife.MLMod;
import com.minelife.util.MLConfig;
import com.minelife.util.PlayerHelper;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ModPermission extends MLMod {

    private static MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventHandler());

        if (event.getSide() == Side.CLIENT) return;

        try {
            config = new MLConfig("permissions");
            setupConfig();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandPermission());
    }

    public static List<String> getPermissions(UUID playerID) {
        List<String> groupPerms = Lists.newArrayList();
        getGroups(playerID).forEach(g -> groupPerms.addAll(getPermissions(g)));
        List<String> playerPerms = Lists.newArrayList();
        if (config.contains("users." + playerID.toString() + ".permissions"))
            playerPerms.addAll(config.getStringList("users." + playerID.toString() + ".permissions"));

        playerPerms.forEach(p -> {
            if (p.startsWith("-")) {
                groupPerms.remove(p.substring(1));
            }
        });

        groupPerms.addAll(playerPerms);
        Set<String> perms = Sets.newTreeSet();
        perms.addAll(groupPerms);
        groupPerms.clear();
        groupPerms.addAll(perms);
        return groupPerms;
    }

    public static List<String> getPlayerPermissions(UUID playerID) {
        List<String> playerPerms = Lists.newArrayList();
        if (config.contains("users." + playerID.toString() + ".permissions"))
            playerPerms.addAll(config.getStringList("users." + playerID.toString() + ".permissions"));
        Set<String> perms = Sets.newTreeSet();
        perms.addAll(playerPerms);
        playerPerms.clear();
        playerPerms.addAll(perms);
        return playerPerms;
    }

    public static List<String> getGroupPermissions(String group) {
        List<String> groupPerms = Lists.newArrayList();
        if (config.contains("groups." + group + ".permissions"))
            groupPerms.addAll(config.getStringList("groups." + group + ".permissions"));
        Set<String> perms = Sets.newTreeSet();
        perms.addAll(groupPerms);
        groupPerms.clear();
        groupPerms.addAll(perms);
        return groupPerms;
    }

    public static List<String> getPermissions(String group) {
        List<String> permissions = Lists.newArrayList();
        if (!config.contains("groups." + group)) return permissions;
        List<String> inheritedGroups = Lists.newArrayList();
        getInheritedGroups(group, inheritedGroups);
        List<String> toRemove = Lists.newArrayList();
        inheritedGroups.forEach(g -> {
            if (g.startsWith("-")) toRemove.add(g);
        });
        inheritedGroups.removeAll(toRemove);

        Set<String> groups = Sets.newTreeSet();
        groups.add(group);
        groups.addAll(inheritedGroups);

        for (String g : groups) {
            if (config.contains("groups." + g + ".permissions"))
                permissions.addAll(config.getStringList("groups." + g + ".permissions"));
        }

        toRemove.clear();
        permissions.forEach(p -> {
            if (p.startsWith("-")) {
                toRemove.add(p.substring(1));
                toRemove.add(p);
            }
        });

        permissions.removeAll(toRemove);
        Set<String> perms = Sets.newTreeSet();
        perms.addAll(permissions);
        permissions.clear();
        permissions.addAll(perms);

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
        if (!config.contains("users." + playerID.toString() + ".groups")) {
            List<String> defaultGroup = Lists.newArrayList();
            if(getDefaultGroup() != null) defaultGroup.add(getDefaultGroup());
            return defaultGroup;
        }
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

    public static List<String> getGroups() {
        Set<String> groups = Sets.newTreeSet();
        for (String group : config.getConfigurationSection("groups").getKeys(false)) {
            groups.add(group);
        }
        List<String> groupsArray = Lists.newArrayList();
        groupsArray.addAll(groups);
        return groupsArray;
    }

    public static boolean hasPermission(UUID playerID, String permission) {
        return PlayerHelper.getPlayer(playerID) != null && PlayerHelper.isOp(PlayerHelper.getPlayer(playerID)) ||
                getPermissions(playerID).contains(permission);
    }

    public static String getPrefix(String group) {
        return config.getString("groups." + group + ".prefix", "");
    }

    public static String getSuffix(String group) {
        return config.getString("groups." + group + ".suffix", "");
    }

    public static String getPrefix(UUID playerID) {
        return config.getString("users." + playerID.toString() + ".prefix", "");
    }

    public static String getSuffix(UUID playerID) {
        return config.getString("users." + playerID.toString() + ".suffix", "");
    }

    /**
     *
     *
     *
     *
     */
    private void setupConfig() {
        config.addDefault("groups.default.default", true);
        config.addDefault("groups.default.permissions", Arrays.asList("estate.pvp"));
        config.addDefault("groups.moderator.inheritance", Arrays.asList("default"));
        config.addDefault("groups.moderator.prefix", TextFormatting.DARK_BLUE.toString() + "[Moderator]" + TextFormatting.RESET.toString());
        config.addDefault("groups.admin.inheritance", Arrays.asList("moderator"));
        config.addDefault("groups.admin.prefix", TextFormatting.DARK_RED.toString() + "[Admin]" + TextFormatting.RESET.toString());
        config.addDefault("users", Lists.newArrayList());
        config.addDefault("chat-format", "&7{DISPLAYNAME}&7: &7{MESSAGE}");
        config.save();
    }

    public static MLConfig getConfig() {
        return config;
    }

    public static void setPlayerPrefix(UUID player, String prefix) {
        getConfig().set("users." + player.toString() + ".prefix", prefix);
        getConfig().save();
    }

    public static void setPlayerSuffix(UUID player, String suffix) {
        getConfig().set("users." + player.toString() + ".suffix", suffix);
        getConfig().save();
    }

    public static void setGroupPrefix(String group, String prefix) throws Exception {
        String groupName = getGroups().stream().filter(g -> g.equalsIgnoreCase(group)).findFirst().orElse(null);
        if (groupName == null) throw new Exception("Group not found.");
        getConfig().set("groups." + groupName + ".prefix", prefix);
        getConfig().save();
    }

    public static void setGroupSuffix(String group, String suffix) throws Exception {
        String groupName = getGroups().stream().filter(g -> g.equalsIgnoreCase(group)).findFirst().orElse(null);
        if (groupName == null) throw new Exception("Group not found.");
        getConfig().set("groups." + groupName + ".suffix", suffix);
        getConfig().save();
    }

    public static void addPlayerPermission(UUID player, String permission) {
        List<String> permissions = getPlayerPermissions(player);
        Set<String> permsSet = Sets.newTreeSet();
        permsSet.addAll(permissions);
        permsSet.add(permission);
        permissions.clear();
        permissions.addAll(permsSet);
        getConfig().set("users." + player.toString() + ".permissions", permissions);
        getConfig().save();
    }

    public static void removePlayerPermission(UUID player, String permission) {
        List<String> permissions = getPlayerPermissions(player);
        Set<String> permsSet = Sets.newTreeSet();
        permsSet.addAll(permissions);
        permsSet.remove(permission);
        permissions.clear();
        permissions.addAll(permsSet);
        getConfig().set("users." + player.toString() + ".permissions", permissions);
        getConfig().save();
    }

    public static void addGroupPermission(String group, String permission) throws Exception {
        String groupName = getGroups().stream().filter(g -> g.equalsIgnoreCase(group)).findFirst().orElse(null);
        if (groupName == null) throw new Exception("Group not found.");
        List<String> permissions = getGroupPermissions(groupName);
        Set<String> permsSet = Sets.newTreeSet();
        permsSet.addAll(permissions);
        permsSet.add(permission);
        permissions.clear();
        permissions.addAll(permsSet);
        getConfig().set("groups." + groupName + ".permissions", permissions);
        getConfig().save();
    }

    public static void removeGroupPermission(String group, String permission) throws Exception {
        String groupName = getGroups().stream().filter(g -> g.equalsIgnoreCase(group)).findFirst().orElse(null);
        if (groupName == null) throw new Exception("Group not found.");
        List<String> permissions = getGroupPermissions(groupName);
        Set<String> permsSet = Sets.newTreeSet();
        permsSet.addAll(permissions);
        permsSet.remove(permission);
        permissions.clear();
        permissions.addAll(permsSet);
        getConfig().set("groups." + groupName + ".permissions", permissions);
        getConfig().save();
    }

    public static void addGroupToPlayer(UUID playerID, String group) throws Exception {
        String groupName = getGroups().stream().filter(g -> g.equalsIgnoreCase(group)).findFirst().orElse(null);
        if (groupName == null) throw new Exception("Group not found.");
        List<String> groups = getGroups(playerID);
        groups.add(group);
        Set<String> groupsSet = Sets.newTreeSet();
        groupsSet.addAll(groups);
        groups.clear();
        groups.addAll(groupsSet);
        getConfig().set("users." + playerID.toString() + ".groups", groups);
        getConfig().save();
    }

    public static void removeGroupFromPlayer(UUID playerID, String group) throws Exception {
        String groupName = getGroups().stream().filter(g -> g.equalsIgnoreCase(group)).findFirst().orElse(null);
        if (groupName == null) throw new Exception("Group not found.");
        List<String> groups = getGroups(playerID);
        groups.remove(group);
        Set<String> groupsSet = Sets.newTreeSet();
        groupsSet.addAll(groups);
        groups.clear();
        groups.addAll(groupsSet);
        getConfig().set("users." + playerID.toString() + ".groups", groups);
        getConfig().save();
    }

    public static void setPlayerGroup(UUID playerID, String group) throws Exception {
        String groupName = getGroups().stream().filter(g -> g.equalsIgnoreCase(group)).findFirst().orElse(null);
        if (groupName == null) throw new Exception("Group not found.");
        List<String> groups = Lists.newArrayList();
        groups.add(group);
        getConfig().set("users." + playerID.toString() + ".groups", groups);
        getConfig().save();
    }

}
