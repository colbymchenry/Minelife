package com.minelife.permission;

import com.google.common.collect.Lists;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.List;
import java.util.UUID;

public class CommandPermission extends MLCommand {

    @Override
    public String getName() {
        return "p";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        sender.sendMessage(new TextComponentString("/p reload"));
        sender.sendMessage(new TextComponentString("/p user <user> prefix <prefix>"));
        sender.sendMessage(new TextComponentString("/p user <user> suffix <suffix>"));
        sender.sendMessage(new TextComponentString("/p user <user> add <permission> [world]"));
        sender.sendMessage(new TextComponentString("/p user <user> remove <permission> [world]"));
        sender.sendMessage(new TextComponentString("/p user <user> group list"));
        sender.sendMessage(new TextComponentString("/p user <user> group add <group> [world]"));
        sender.sendMessage(new TextComponentString("/p user <user> group set <group> [world]"));
        sender.sendMessage(new TextComponentString("/p user <user> group remove <group> [world]"));
        sender.sendMessage(new TextComponentString("/p group <group> prefix <prefix>"));
        sender.sendMessage(new TextComponentString("/p group <group> suffix <suffix>"));
        sender.sendMessage(new TextComponentString("/p group <group> add <permission> [world]"));
        sender.sendMessage(new TextComponentString("/p group <group> remove <permission> [world]"));
        return null;
    }

    @Override
    public List<String> getAliases() {
        return Lists.newArrayList();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return !(sender instanceof EntityPlayerMP) || ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "permissions");
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public synchronized void runAsync(MinecraftServer server, ICommandSender sender, String[] args) throws Exception {
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reload")) {
                ModPermission.getConfig().reload();
                sender.sendMessage(new TextComponentString("permissions.yml reloaded!"));
                return;
            }
            getUsage(sender);
        }

        if (args.length < 3) {
            getUsage(sender);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "user": {
                UUID playerUUID = UUIDFetcher.get(args[1]);
                switch (args[2].toLowerCase()) {
                    case "prefix": {
                        ModPermission.setPlayerPrefix(playerUUID, args.length < 4 ? null : args[3]);
                        break;
                    }
                    case "suffix": {
                        ModPermission.setPlayerSuffix(playerUUID, args.length < 4 ? null : args[3]);
                        break;
                    }
                    case "add": {
                        ModPermission.addPlayerPermission(playerUUID, args[3]);
                        break;
                    }
                    case "remove": {
                        ModPermission.removePlayerPermission(playerUUID, args[3]);
                        break;
                    }
                    case "group": {
                        if (args.length < 5) {
                            getUsage(sender);
                            return;
                        }
                        switch (args[3].toLowerCase()) {
                            case "add": {
                                ModPermission.addGroupToPlayer(playerUUID, args[4]);
                                break;
                            }
                            case "remove": {
                                ModPermission.removeGroupFromPlayer(playerUUID, args[4]);
                                break;
                            }
                            case "set": {
                                ModPermission.setPlayerGroup(playerUUID, args[4]);
                                break;
                            }
                            default: {
                                getUsage(sender);
                                break;
                            }
                        }
                        break;
                    }
                    default: {
                        getUsage(sender);
                        break;
                    }
                }
                break;
            }
            case "group": {
                String group = ModPermission.getGroups().stream().filter(g -> g.equalsIgnoreCase(args[1])).findFirst().orElse(null);
                switch (args[2].toLowerCase()) {
                    case "prefix": {
                        ModPermission.setGroupPrefix(group, args.length < 4 ? null : args[3]);
                        break;
                    }
                    case "suffix": {
                        ModPermission.setGroupSuffix(group, args.length < 4 ? null : args[3]);
                        break;
                    }
                    case "add": {
                        ModPermission.addGroupPermission(group, args[3]);
                        break;
                    }
                    case "remove": {
                        ModPermission.removeGroupPermission(group, args[3]);
                        break;
                    }
                    default: {
                        getUsage(sender);
                        break;
                    }
                }
                break;
            }
        }
    }
}
