package com.minelife.economy.server;

import com.google.common.collect.Lists;
import com.minelife.economy.ModEconomy;
import com.minelife.util.NumberConversions;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.UUID;

public class CommandBalance extends MLCommand {

    @Override
    public String getName() {
        return "balance";
    }

    @Override
    public List<String> getAliases() {
        return Lists.newArrayList("bal");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/balance <player>";
    }

    @Override
    public synchronized void runAsync(MinecraftServer server, ICommandSender sender, String[] args) throws Exception {
        EntityPlayerMP player = (EntityPlayerMP) sender;

        if(args.length < 1) {
            sender.sendMessage(new TextComponentString("$" + NumberConversions.format(ModEconomy.getBalanceCashPiles(player.getUniqueID()))));
            return;
        }

        UUID playerID = UUIDFetcher.get(args[0]);

        if(playerID == null) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player not found."));
            return;
        }

        sender.sendMessage(new TextComponentString("$" + NumberConversions.format(ModEconomy.getBalanceCashPiles(playerID))));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }
}
