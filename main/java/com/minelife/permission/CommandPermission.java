package com.minelife.permission;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class CommandPermission implements ICommand {

    @Override
    public String getCommandName() {
        return "permissions";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        List<String> commands = Lists.newArrayList();
        commands.add("add -g <group> <perm-node>");
        commands.add("remove -g <group> <perm-node>");
        commands.add("add -p <player> <perm-node>");
        commands.add("remove -p <player> <perm-node>");
        commands.add("setgroup <player> <group>");

        String msg = "";
        for (String cmd : commands) msg += "permissions " + cmd + "\n";

        return msg;
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        try {
            boolean add, group;

            if(args.length == 0) {
                sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
                return;
            }

            add = args[0].equalsIgnoreCase("add");
            group = args[1].equalsIgnoreCase("-g");

            // setting group
            if (args[0].equalsIgnoreCase("setgroup")) {
                Group defaultGroup = Group.getGroups().stream().filter(Group::isDefaultGroup).findFirst().orElse(null);
                Group groupFromCMD = Group.getGroups().stream().filter(g -> g.getName().equalsIgnoreCase(args[2])).findFirst().orElse(defaultGroup);
                new Player(UUIDFetcher.get(args[1])).setGroup(groupFromCMD);
            }

            // adding permissions
            if (add && group) new Group(args[2]).addPermission(args[3]);
            if (add && !group) new Player(UUIDFetcher.get(args[2])).addPermission(args[3]);

            // removing permissions
            if (!add && group) new Group(args[2]).removePermission(args[3]);
            if (!add && !group) new Player(UUIDFetcher.get(args[2])).removePermission(args[3]);
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText(e.getMessage()));
            e.printStackTrace();
            Minelife.getLogger().log(Level.SEVERE, "", e);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        if (!(sender instanceof EntityPlayer)) return true;
        EntityPlayerMP player = (EntityPlayerMP) sender;
        return new Player(player.getUniqueID()).hasPermission("permissions.edit");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        MinecraftServer server = MinecraftServer.getServer();

        List<String> usernames = Lists.newArrayList();

        server.getConfigurationManager().playerEntityList.forEach(player -> usernames.add(((EntityPlayerMP) player).getDisplayName()));

        List<String> list = Lists.newArrayList();
        if (args[0].equalsIgnoreCase("setgroup")) {
            if (args.length == 2)
                // add player names for /permissions setgroup <player>
                list.addAll(usernames);
            else if (args.length == 3)
                // add group names for /permissions setgroup <player> <group>
                Group.getGroups().forEach(group -> list.add(group.getName()));
        } else {
            if (args.length == 3 && args[1].equalsIgnoreCase("-g"))
                // add all groups for add or remove command
                for (Group g : Group.getGroups()) list.add(g.getName());
            else if (args.length == 3 && args[1].equalsIgnoreCase("-p"))
                // add all players for add or remove command
                list.addAll(usernames);
        }
        return list;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(Object o)
    {
        return 0;
    }
}
