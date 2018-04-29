package com.minelife.economy.server;

import com.google.common.collect.Maps;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.tileentity.TileEntityCash;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.MapHelper;
import com.minelife.util.NumberConversions;
import com.minelife.util.StringHelper;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.NameFetcher;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class CommandBalanceTop extends MLCommand {

    @Override
    public String getName() {
        return "baltop";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/baltop";
    }

    @Override
    public synchronized void runAsync(MinecraftServer server, ICommandSender sender, String[] args) throws Exception {
        Map<UUID, Long> totals = Maps.newHashMap();
        EntityPlayerMP player = (EntityPlayerMP) sender;

        Map<UUID, String> names = Maps.newHashMap();
        for (Estate estate : ModRealEstate.getLoadedEstates()) {
            if (estate.getRenterID() != null) {
                if (!names.containsKey(estate.getRenterID()))
                    names.put(estate.getRenterID(), NameFetcher.get(estate.getRenterID()));
            }
            if (!names.containsKey(estate.getOwnerID()))
                names.put(estate.getOwnerID(), NameFetcher.get(estate.getOwnerID()));
        }


        MLCommand.scheduledTasks.add(() -> {
            for (Estate estate : ModRealEstate.getLoadedEstates()) {
                if (estate.getRenterID() != null) {
                    if (!totals.containsKey(estate.getRenterID())) {
                        totals.put(estate.getRenterID(), ModEconomy.getBalanceCashPiles(TileEntityCash.getCashPiles(estate)));
                    } else {
                        totals.put(estate.getRenterID(), totals.get(estate.getRenterID()) + ModEconomy.getBalanceCashPiles(TileEntityCash.getCashPiles(estate)));
                    }
                } else {
                    if (!totals.containsKey(estate.getOwnerID())) {
                        totals.put(estate.getOwnerID(), ModEconomy.getBalanceCashPiles(TileEntityCash.getCashPiles(estate)));
                    } else {
                        totals.put(estate.getOwnerID(), totals.get(estate.getOwnerID()) + ModEconomy.getBalanceCashPiles(TileEntityCash.getCashPiles(estate)));
                    }
                }
            }

            StringBuilder builder = new StringBuilder("0");
            player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&6&l-----&c&l[TOP DAWGS]&6&l-----", '&')));
            MapHelper.sortByValue(totals).forEach((playerID, balance) -> {
                int i = Integer.parseInt(builder.toString());
                i++;
                if(i > 11) return;
                builder.deleteCharAt(0);
                builder.append("" + i);
                player.sendMessage(new TextComponentString(TextFormatting.GOLD + names.get(playerID) + ": " + TextFormatting.RED + "$" + NumberConversions.format(balance)));
            });
        });

    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
