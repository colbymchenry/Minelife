package com.minelife.police.server;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.police.ModPolice;
import com.minelife.region.server.Region;
import com.minelife.util.PlayerHelper;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class CommandPolice implements ICommand {

    @Override
    public String getCommandName() {
        return "police";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("/police prison set region"));
        sender.addChatMessage(new ChatComponentText("/police prison set exit"));
        sender.addChatMessage(new ChatComponentText("/police prison set enter"));
        sender.addChatMessage(new ChatComponentText("/police prison delete"));
        sender.addChatMessage(new ChatComponentText("/police prison"));
        sender.addChatMessage(new ChatComponentText("/police lockup <player>"));
        sender.addChatMessage(new ChatComponentText("/police pardon <player>"));
        return "";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(final ICommandSender sender, final String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        String cmd = args[0];
        final EntityPlayerMP player = (EntityPlayerMP) sender;

        switch (cmd.toLowerCase()) {
            case "prison": {
                prisonCmd(player, args);
                return;
            }
            case "lockup": {
                if (args.length == 0) {
                    getCommandUsage(player);
                    return;
                }

                pool.submit(() -> {
                    lockupCmd(player, args, UUIDFetcher.get(args[1]));
                });
                return;
            }
            case "pardon": {
                if (args.length == 0) {
                    getCommandUsage(player);
                    return;
                }

                pool.submit(() -> {
                    pardonCmd(player, args, UUIDFetcher.get(args[1]));
                });
                return;
            }
            default: {
                getCommandUsage(sender);
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayer;
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

    private void prisonCmd(EntityPlayerMP player, String[] args) {
        if (args.length == 0) {
            teleportToPrison:
            {
                Vec3 tpVec = ModPolice.getServerProxy().getPrisonEntrance();
                if (tpVec == null) {
                    player.addChatComponentMessage(new ChatComponentText("Teleport location not defined."));
                } else {
                    ModPolice.getServerProxy().sendToPrison(player);
                }
            }

            return;
        }

        Region region = Region.getRegionAt(player.worldObj, Vec3.createVectorHelper(player.posX, player.posY, player.posZ));

        if (region == null) {
            player.addChatComponentMessage(new ChatComponentText("No region was found at your location."));
            return;
        }

        if (args[1].equalsIgnoreCase("set")) {
            if (args[2].equalsIgnoreCase("region")) {
                ModPolice.getServerProxy().setPrisonRegion(region);
                player.addChatComponentMessage(new ChatComponentText("Prison yard region has been set!"));
            } else if (args[2].equalsIgnoreCase("exit")) {
                ModPolice.getServerProxy().setPrisonExit(player.posX, player.posY, player.posZ);
                player.addChatComponentMessage(new ChatComponentText("Prison yard exit has been set!"));
            } else if (args[2].equalsIgnoreCase("enter")) {
                ModPolice.getServerProxy().setPrisonEntrance(player.posX, player.posY, player.posZ);
                player.addChatComponentMessage(new ChatComponentText("Prison yard entrance has been set!"));
            }
        } else if (args[1].equalsIgnoreCase("delete")) {
            ModPolice.getServerProxy().setPrisonRegion(null);
            player.addChatComponentMessage(new ChatComponentText("Prison yard has been removed!"));
        } else {
            getCommandUsage(player);
        }
    }

    private synchronized void lockupCmd(EntityPlayerMP player, String[] args, UUID playerUUID) {
        if (playerUUID == null) {
            player.addChatComponentMessage(new ChatComponentText("Player not found."));
            return;
        }

        if (ModPolice.getServerProxy().getTickets(playerUUID).isEmpty()) {
            player.addChatComponentMessage(new ChatComponentText("That player has no charges against them."));
            return;
        }

        if (PlayerHelper.getPlayer(playerUUID) == null) {
            try {
                Minelife.SQLITE.query("INSERT INTO policelockup (playerUUID) VALUES ('" + playerUUID.toString() + "', '1')");
            } catch (SQLException e) {
                Minelife.handle_exception(e, player);
            }
            player.addChatComponentMessage(new ChatComponentText("The player is not online, but will be sent to the prison upon logging in."));
        } else {
            ModPolice.getServerProxy().sendToPrison(PlayerHelper.getPlayer(playerUUID));
            player.addChatComponentMessage(new ChatComponentText("Player has been sent to the prison."));
        }

    }

    private synchronized void pardonCmd(EntityPlayerMP player, String[] args, UUID playerUUID) {
        if (playerUUID == null) {
            player.addChatComponentMessage(new ChatComponentText("Player not found."));
            return;
        }

        if (ModPolice.getServerProxy().getTickets(playerUUID).isEmpty()) {
            player.addChatComponentMessage(new ChatComponentText("That player has no charges against them."));
            return;
        }

        EntityPlayerMP playerFromCMD = PlayerHelper.getPlayer(playerUUID);
        try {
            if (playerFromCMD == null) {
                Minelife.SQLITE.query("INSERT INTO policepardon (playerUUID) VALUES ('" + playerUUID.toString() + "', '1')");
                player.addChatComponentMessage(new ChatComponentText("The player is not online, but will be pardoned from the prison upon logging in."));
            } else {
                if (ModPolice.getServerProxy().getPrisonRegion().contains(playerFromCMD.worldObj.getWorldInfo().getWorldName(), playerFromCMD.posX, playerFromCMD.posY, playerFromCMD.posZ)) {
                    ModPolice.getServerProxy().sendToPrisonExit(PlayerHelper.getPlayer(playerUUID));
                }
                player.addChatComponentMessage(new ChatComponentText("Player has been pardoned."));
            }

            Minelife.SQLITE.query("DELETE * FROM policetickets WHERE playerUUID='" + playerUUID.toString() + "'");
        } catch (SQLException e) {
            Minelife.handle_exception(e, player);
        }
    }

    private static final ExecutorService pool = Executors.newFixedThreadPool(10);


}
