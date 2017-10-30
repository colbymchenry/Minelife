package com.minelife.permission;

import com.google.common.collect.Lists;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.util.List;
import java.util.UUID;

public class CommandPermission extends MLCommand {

    @Override
    public String getCommandName() {
        return "p";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("/p user <user> prefix <prefix>"));
        sender.addChatMessage(new ChatComponentText("/p user <user> suffix <suffix>"));
        sender.addChatMessage(new ChatComponentText("/p user <user> add <permission> [world]"));
        sender.addChatMessage(new ChatComponentText("/p user <user> remove <permission> [world]"));
        sender.addChatMessage(new ChatComponentText("/p user <user> group list"));
        sender.addChatMessage(new ChatComponentText("/p user <user> group add <group> [world]"));
        sender.addChatMessage(new ChatComponentText("/p user <user> group set <group> [world]"));
        sender.addChatMessage(new ChatComponentText("/p user <user> group remove <group> [world]"));
        sender.addChatMessage(new ChatComponentText("/p group <group> prefix <prefix>"));
        sender.addChatMessage(new ChatComponentText("/p group <group> suffix <suffix>"));
        sender.addChatMessage(new ChatComponentText("/p group <group> add <permission> [world]"));
        sender.addChatMessage(new ChatComponentText("/p group <group> remove <permission> [world]"));
        return null;
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return !(sender instanceof EntityPlayerMP) || ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "permissions");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    // TODO: Test these commands
    @Override
    public synchronized void execute(ICommandSender sender, String[] args) throws Exception {
        if(args.length < 4) {
            getCommandUsage(sender);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "user": {
                UUID playerUUID = UUIDFetcher.get(args[1]);
                switch (args[2].toLowerCase()) {
                    case "prefix": {
                        ModPermission.setPlayerPrefix(playerUUID, args[3]);
                        break;
                    }
                    case "suffix": {
                        ModPermission.setPlayerSuffix(playerUUID, args[3]);
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
                        if(args.length < 5) {
                            getCommandUsage(sender);
                            return;
                        }
                        switch(args[3].toLowerCase()) {
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
                                getCommandUsage(sender);
                                break;
                            }
                        }
                        break;
                    }
                    default: {
                        getCommandUsage(sender);
                        break;
                    }
                }
                break;
            }
            case "group": {
                String group = ModPermission.getGroups().stream().filter(g -> g.equalsIgnoreCase(args[1])).findFirst().orElse(null);
                switch (args[2].toLowerCase()) {
                    case "prefix": {
                        ModPermission.setGroupPrefix(group, args[3]);
                        break;
                    }
                    case "suffix": {
                        ModPermission.setGroupSuffix(group, args[3]);
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
                        getCommandUsage(sender);
                        break;
                    }
                }
                break;
            }
        }
    }
}
