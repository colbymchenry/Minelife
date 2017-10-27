package com.minelife.permission;

import com.google.common.collect.Lists;
import com.minelife.economy.ModEconomy;
import com.minelife.util.PlayerHelper;
import com.minelife.util.server.Callback;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @https://github.com/PEXPlugins/PermissionsEx/wiki/Commands
 */

public class CommandPermissionOld implements ICommand, Callback {

    @Override
    public String getCommandName() {
        return "permissions";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("p");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        switch (args[0].toLowerCase()) {
            case "user": {
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText("Not enough arguments."));
                    return;
                }

                UUIDFetcher.asyncFetchServer(args[1], this, args, sender);
                break;
            }
            /**
             *
             */
            case "group": {
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText("Not enough arguments."));
                    return;
                }

                String group = ModPermission.getGroups().stream().filter(g -> g.equalsIgnoreCase(args[1])).findFirst().orElse(null);

                if (group == null) {
                    sender.addChatMessage(new ChatComponentText("Group not found."));
                    return;
                }
                /**
                 * Handle all group commands
                 */
                switch (args[2].toLowerCase()) {
                    case "prefix": {
                        ModPermission.getConfig().set("groups." + group + ".prefix", args[3]);
                        break;
                    }
                    case "suffix": {
                        ModPermission.getConfig().set("groups." + group + ".suffix", args[3]);
                        break;
                    }
                    case "add": {
                        List<String> permissions = ModPermission.getGroupPermissions(group);
                        permissions.add(args[3]);
                        ModPermission.getConfig().set("groups." + group + ".permissions", permissions);
                        break;
                    }
                    case "remove": {
                        List<String> permissions = ModPermission.getGroupPermissions(group);
                        permissions.remove(args[3]);
                        ModPermission.getConfig().set("groups." + group + ".permissions", permissions);
                        break;
                    }
                    default: {
                        sender.addChatMessage(new ChatComponentText("/p group <group> prefix <prefix>"));
                        sender.addChatMessage(new ChatComponentText("/p group <group> suffix <suffix>"));
                        sender.addChatMessage(new ChatComponentText("/p group <group> add <node>"));
                        sender.addChatMessage(new ChatComponentText("/p group <group> remove <node>"));
                        break;
                    }
                }
                break;
            }
            /**
             *
             */
            case "reload": {
                break;
            }
            default: {
                sender.addChatMessage(new ChatComponentText("/p user"));
                sender.addChatMessage(new ChatComponentText("/p group"));
                break;
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if (!(sender instanceof EntityPlayerMP)) return true;
        return PlayerHelper.isOp((EntityPlayerMP) sender);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public void callback(Object... objects) {
        UUID playerID = (UUID) objects[0];
        String playerName = (String) objects[1];
        String[] args = (String[]) objects[2];
        ICommandSender sender = (ICommandSender) objects[3];

        switch (args[2].toLowerCase()) {
            case "prefix": {
                ModPermission.getConfig().set("users." + playerID.toString() + ".prefix", args[3]);
                sender.addChatMessage(new ChatComponentText(String.format("'%1$s' will now display as %2$s", playerName, args[3] + playerName)));
                break;
            }
            case "suffix": {
                ModPermission.getConfig().set("users." + playerID.toString() + ".suffix", args[3]);
                sender.addChatMessage(new ChatComponentText(String.format("'%1$s' will now display as %2$s", playerName, playerName + args[3])));
                break;
            }
            case "add": {
                List<String> permissions = ModPermission.getPlayerPermissions(playerID);
                permissions.add(args[3]);
                ModPermission.getConfig().set("users." + playerID.toString() + ".permissions", permissions);
                sender.addChatMessage(new ChatComponentText(String.format("Added node '%1$s' to %2$s", args[3], playerName)));
                break;
            }
            case "remove": {
                List<String> permissions = ModPermission.getPlayerPermissions(playerID);
                permissions.remove(args[3]);
                ModPermission.getConfig().set("users." + playerID.toString() + ".permissions", permissions);
                sender.addChatMessage(new ChatComponentText(String.format("Removed node '%1$s' from %2$s", args[3], playerName)));
                break;
            }
            case "group": {
                switch (args[3].toLowerCase()) {
                    case "list": {
                        ModPermission.getGroups(playerID).forEach(g -> sender.addChatMessage(new ChatComponentText(g)));
                        break;
                    }
                    case "add": {
                        List<String> groups = ModPermission.getGroups(playerID);
                        String groupName = ModPermission.getGroups().stream().filter(g -> g.equalsIgnoreCase(args[4])).findFirst().orElse(null);
                        if (groupName != null) {
                            groups.add(groupName);
                            sender.addChatMessage(new ChatComponentText(String.format("%1$s added to group %2$s", playerName, groupName)));
                            ModPermission.getConfig().set("users." + playerID.toString() + ".groups", groups);
                        } else {
                            sender.addChatMessage(new ChatComponentText("Group not found."));
                        }
                        break;
                    }
                    case "set": {
                        List<String> groups = ModPermission.getGroups(playerID);
                        String groupName = ModPermission.getGroups().stream().filter(g -> g.equalsIgnoreCase(args[4])).findFirst().orElse(null);
                        if (groupName != null) {
                            groups.clear();
                            groups.add(groupName);
                            sender.addChatMessage(new ChatComponentText(String.format("%1$s is now ONLY in group %2$s", playerName, groupName)));
                            ModPermission.getConfig().set("users." + playerID.toString() + ".groups", groups);
                        } else {
                            sender.addChatMessage(new ChatComponentText("Group not found."));
                        }
                        break;
                    }
                    case "remove": {
                        List<String> groups = ModPermission.getGroups(playerID);
                        String groupName = ModPermission.getGroups().stream().filter(g -> g.equalsIgnoreCase(args[4])).findFirst().orElse(null);
                        if (groupName != null) {
                            groups.remove(groupName);
                            sender.addChatMessage(new ChatComponentText(String.format("%1$s removed from group %2$s", playerName, groupName)));
                            ModPermission.getConfig().set("users." + playerID.toString() + ".groups", groups);
                        } else {
                            sender.addChatMessage(new ChatComponentText("Group not found."));
                        }
                        break;
                    }
                    default: {
                        sender.addChatMessage(new ChatComponentText("/p user group list"));
                        sender.addChatMessage(new ChatComponentText("/p user group add <group>"));
                        sender.addChatMessage(new ChatComponentText("/p user group set <group>"));
                        sender.addChatMessage(new ChatComponentText("/p user group remove <group>"));
                        break;
                    }
                }
                break;
            }
            default: {
                sender.addChatMessage(new ChatComponentText("/p user prefix <prefix>"));
                sender.addChatMessage(new ChatComponentText("/p user suffix <suffix>"));
                sender.addChatMessage(new ChatComponentText("/p user add <permission>"));
                sender.addChatMessage(new ChatComponentText("/p user remove <permission>"));
                break;
            }
        }
    }

}
