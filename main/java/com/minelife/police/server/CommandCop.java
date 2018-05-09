package com.minelife.police.server;

import com.google.common.collect.Lists;
import com.minelife.essentials.Location;
import com.minelife.essentials.server.commands.Spawn;
import com.minelife.police.cop.EntityCop;
import com.minelife.police.ModPolice;
import com.minelife.police.Prisoner;
import com.minelife.tdm.SavedInventory;
import com.minelife.util.PlayerHelper;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CommandCop extends MLCommand {

    @Override
    public String getName() {
        return "cop";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/cop addspawn\n/cop delspawn\n/cop reset\n/cop bail <player>";
    }

    @Override
    public synchronized void runAsync(MinecraftServer server, ICommandSender sender, String[] args) throws Exception {
        List<String> spawns = ModPolice.getConfig().getStringList("PoliceSpawnPoints") != null ? ModPolice.getConfig().getStringList("PoliceSpawnPoints") : Lists.newArrayList();

        if(args[0].equalsIgnoreCase("addspawn")) {
            spawns.add(sender.getPosition().getX() + "," + sender.getPosition().getY() + "," + sender.getPosition().getZ());
            sender.sendMessage(new TextComponentString("Spawn added!"));
        } else if(args[0].equalsIgnoreCase("delspawn")) {
            spawns.remove(sender.getPosition().getX() + "," + sender.getPosition().getY() + "," + sender.getPosition().getZ());
            sender.sendMessage(new TextComponentString("Spawn deleted!"));
        } else if(args[0].equalsIgnoreCase("reset")) {
            ServerProxy.cleanCops(sender.getEntityWorld());
            ServerProxy.spawnCops(sender.getEntityWorld());
            sender.sendMessage(new TextComponentString("Reset!"));
        } else if(args[0].equalsIgnoreCase("bail")) {
            if (args.length != 2) {
                sender.sendMessage(new TextComponentString(getUsage(sender)));
                return;
            }

            UUID playerID = UUIDFetcher.get(args[1]);

            if (playerID != null) {

                // kick off cop if player is riding cop
                EntityCop cop = ServerProxy.getCopsForWorld(sender.getEntityWorld()).stream().filter(entityCop -> entityCop.getCarryingPlayer() != null && entityCop.getCarryingPlayer().getUniqueID().equals(playerID)).findFirst().orElse(null);
                if (cop != null) {
                    cop.getCarryingPlayer().dismountRidingEntity();
                    cop.setAttackTarget(null);
                    cop.setChasingPlayer(null);
                    cop.getNavigator().clearPath();
                }

                Prisoner prisoner = Prisoner.getPrisoner(playerID);

                // if a prisoner take back to spawn and give items back
                if (prisoner != null) {
                    if(PlayerHelper.getPlayer(playerID) != null) {
                        prisoner.freePrisoner(PlayerHelper.getPlayer(playerID));
                    } else sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player is not online."));
                }

            }
        } else if(args[0].equalsIgnoreCase("reset-inventory")) {
            if (args.length != 2) {
                sender.sendMessage(new TextComponentString(getUsage(sender)));
                return;
            }

            UUID playerID = UUIDFetcher.get(args[1]);
            SavedInventory savedInventory = SavedInventory.hasSavedInventory(playerID) ? new SavedInventory(playerID) : null;
            if(savedInventory != null) {
                MLCommand.scheduledTasks.add(() -> {
                    EntityPlayer toRestore = PlayerHelper.getPlayer(playerID);
                    if(toRestore != null) {
                        toRestore.inventory.clear();
                        savedInventory.getItems().forEach((slot, stack) -> toRestore.inventory.setInventorySlotContents(slot, stack));
                        toRestore.inventoryContainer.detectAndSendChanges();
                        sender.sendMessage(new TextComponentString("Inventory reset!"));
                    }
                });
            }

        } else {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
        }

        ModPolice.getConfig().set("PoliceSpawnPoints", spawns);
        ModPolice.getConfig().save();
    }

}
