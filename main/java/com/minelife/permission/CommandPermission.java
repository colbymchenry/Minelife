package com.minelife.permission;

import com.google.common.collect.Lists;
import com.minelife.economy.ModEconomy;
import com.minelife.util.PlayerHelper;
import com.minelife.util.server.Callback;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CommandPermission implements ICommand, Callback {

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
                UUIDFetcher.asyncFetchServer(args[1], this, true, args, sender);
                break;
            }
            /**
             *
             */
            case "group": {
                String group = args[1];
                /**
                 * Handle all group commands
                 */
                switch (args[2].toLowerCase()) {
                    case "prefix": {

                        break;
                    }
                    case "suffix": {

                        break;
                    }
                    case "create": {

                        break;
                    }
                    case "delete": {

                        break;
                    }
                    case "parents": {
                        break;
                    }
                    case "add": {

                        break;
                    }
                    case "remove": {

                        break;
                    }
                    case "timed": {
                        if(args[3].equalsIgnoreCase("add")) {

                        } else if (args[3].equalsIgnoreCase("remove")) {

                        } else {

                        }
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

                break;
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if(!(sender instanceof EntityPlayerMP)) return true;
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
        boolean isUserCommand = (boolean) objects[2];
        String[] args = (String[]) objects[3];
        ICommandSender sender = (ICommandSender) objects[4];

        if(isUserCommand) {
            switch (args[2].toLowerCase()) {
                case "prefix": {
                    break;
                }
                case "suffix": {
                    break;
                }
                case "delete": {
                    break;
                }
                case "add": {
                    break;
                }
                case "remove": {
                    break;
                }
                case "timed": {
                    if(args[3].equalsIgnoreCase("add")) {

                    } else if(args[3].equalsIgnoreCase("remove")) {

                    } else {

                    }
                    break;
                }
                case "group": {
                    switch(args[3].toLowerCase()) {
                        case "list": {

                            break;
                        }
                        case "add": {

                            break;
                        }
                        case "set": {

                            break;
                        }
                        case "remove": {

                            break;
                        }
                    }
                    break;
                }
            }
        } else {

        }
    }
}
