package com.minelife.tdm.server;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.permission.ModPermission;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.tdm.Arena;
import com.minelife.tdm.Match;
import com.minelife.tdm.network.PacketOpenMatchSearch;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.Map;

public class CommandTDM extends CommandBase {

    @Override
    public String getName() {
        return "tdm";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        sendMessage(sender, "/tdm create <name>");
        sendMessage(sender, "/tdm delete <name>");
        sendMessage(sender, "/tdm setspawn1");
        sendMessage(sender, "/tdm setspawn2");
        sendMessage(sender, "/tdm setlobbyspawn");
        sendMessage(sender, "/tdm setexitspawn <arena>");
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = (EntityPlayerMP) sender;

        if(args.length == 0) {
            Map<String, Match> arenas = Maps.newHashMap();
            Map<String, String> arenaPixels = Maps.newHashMap();
            Arena.ARENAS.forEach(arena -> {
                arenas.put(arena.getName(), arena.getCurrentMatch());
                arenaPixels.put(arena.getName(), arena.getPixels());
            });
            Minelife.getNetwork().sendTo(new PacketOpenMatchSearch(arenas, arenaPixels), player);
            return;
        }

        Estate estate = ModRealEstate.getEstateAt(player.getEntityWorld(), player.getPosition());

        switch (args[0].toLowerCase()) {
            case "create": {
                if(args.length != 2) {
                    getUsage(sender);
                    return;
                }

                if(estate == null) {
                    sendMessage(sender, TextFormatting.RED + "Arena must be within an estate.");
                    return;
                }

                if(Arena.ARENAS.stream().filter(arena -> arena.getEstate().equals(estate)).findFirst().orElse(null) != null) {
                    sendMessage(sender, TextFormatting.RED + "There is already an arena for this estate.");
                    return;
                }

                if(Arena.ARENAS.stream().filter(arena -> arena.getName().equalsIgnoreCase(args[1])).findFirst().orElse(null) != null) {
                    sendMessage(sender, TextFormatting.RED + "There is already an arena with that name.");
                    return;
                }

                try {
                    Arena.createArena(args[1], estate);
                    sendMessage(sender, "Arena created!");
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                    sendMessage(sender, TextFormatting.RED + "An error occurred.");
                }
                break;
            }
            case "delete": {
                if(args.length != 2) {
                    getUsage(sender);
                    return;
                }

                Arena arena = Arena.ARENAS.stream().filter(a -> a.getName().equalsIgnoreCase(args[1])).findFirst().orElse(null);

                if(arena == null) {
                    sendMessage(sender, TextFormatting.RED + "No arena exists with that name.");
                    return;
                }

                arena.delete();
                sendMessage(sender, "Arena deleted.");
                break;
            }
            case "setspawn1": {
                if(estate == null) {
                    sendMessage(sender, TextFormatting.RED + "There is no arena here.");
                    return;
                }

                Arena arena = Arena.ARENAS.stream().filter(a -> a.getEstate().equals(estate)).findFirst().orElse(null);

                if(arena == null) {
                    sendMessage(sender, TextFormatting.RED + "There is no arena here.");
                    return;
                }

                arena.setTeam1Spawn(player.getPosition());
                sendMessage(sender, "Spawn for team 1 set.");
                break;
            }
            case "setspawn2": {
                if(estate == null) {
                    sendMessage(sender, TextFormatting.RED + "There is no arena here.");
                    return;
                }

                Arena arena = Arena.ARENAS.stream().filter(a -> a.getEstate().equals(estate)).findFirst().orElse(null);

                if(arena == null) {
                    sendMessage(sender, TextFormatting.RED + "There is no arena here.");
                    return;
                }

                arena.setTeam2Spawn(player.getPosition());
                sendMessage(sender, "Spawn for team 2 set.");
                break;
            }
            case "setlobbyspawn": {
                if(estate == null) {
                    sendMessage(sender, TextFormatting.RED + "There is no arena here.");
                    return;
                }

                Arena arena = Arena.ARENAS.stream().filter(a -> a.getEstate().equals(estate)).findFirst().orElse(null);

                if(arena == null) {
                    sendMessage(sender, TextFormatting.RED + "There is no arena here.");
                    return;
                }

                arena.setLobbySpawn(player.getPosition());
                sendMessage(sender, "Lobby spawn set.");
                break;
            }
            case "setexitspawn": {
                if(args.length != 2) {
                    getUsage(sender);
                    return;
                }

                Arena arena = Arena.ARENAS.stream().filter(a -> a.getName().equalsIgnoreCase(args[1])).findFirst().orElse(null);

                if(arena == null) {
                    sendMessage(sender, TextFormatting.RED + "No arena exists with that name.");
                    return;
                }

                arena.setExitSpawn(player.getPosition());
                sendMessage(sender, "Exit spawn set.");
                break;
            }
            default: getUsage(sender); break;
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "tdm.arena.setup");
    }

    public static void sendMessage(ICommandSender sender, String msg) {
        sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[TDM] " + TextFormatting.GOLD + msg));
    }

}
