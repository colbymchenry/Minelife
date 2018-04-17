package com.minelife.essentials.server.commands;

import com.minelife.essentials.Location;
import com.minelife.permission.ModPermission;
import com.minelife.util.StringHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.Map;

import static com.minelife.essentials.server.commands.Home.GetHomes;

public class Homes extends CommandBase {

    @Override
    public String getName() {
        return "homes";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/homes";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP Player = (EntityPlayerMP) sender;
        Map<String, Location> homes = GetHomes(Player.getUniqueID());

        Player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&c=-=-=-=[&6HOMES&c]=-=-=-=", '&')));
        int i = 1;
        for (String s : homes.keySet()) {
            Player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&c" + i + ". &6" + s, '&')));
            i++;
        }

    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "home");
    }

}
