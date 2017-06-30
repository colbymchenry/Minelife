package com.minelife.region.server;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.WorldEditHook;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

public class CommandRegion implements ICommand {

    @Override
    public String getCommandName() {
        return "region";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/region create '-s'\n/region delete '-s'\n" + EnumChatFormatting.GOLD + "Use '-s' at the end of command to specify subregion.";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        EntityPlayerMP player = (EntityPlayerMP) sender;

        if (args.length == 0) {
            player.addChatComponentMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        try {
            if (args[0].equalsIgnoreCase("create")) {
                com.sk89q.worldedit.regions.Region selection = WorldEditHook.getSelection(player);

                if (selection == null || selection.getMinimumPoint() == null || selection.getMaximumPoint() == null) {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Error: No valid selection was made."));
                    return;
                }

                int[] min = new int[]{selection.getMinimumPoint().getBlockX(), selection.getMinimumPoint().getBlockY(), selection.getMinimumPoint().getBlockZ()};
                int[] max = new int[]{selection.getMaximumPoint().getBlockX(), selection.getMaximumPoint().getBlockY(), selection.getMaximumPoint().getBlockZ()};
                if (args.length < 2) {
                    Region.createRegion(sender.getEntityWorld().getWorldInfo().getWorldName(), min, max);
                    player.addChatComponentMessage(new ChatComponentText("Region created."));
                } else if (args[1].equalsIgnoreCase("-s")) {
                    SubRegion.createSubRegion(Region.getRegionAt(player.getEntityWorld(), selection.getCenter().getBlockX(),
                            selection.getCenter().getBlockY(), selection.getCenter().getBlockZ()), min, max);
                    player.addChatComponentMessage(new ChatComponentText("SubRegion created."));
                } else {
                    player.addChatComponentMessage(new ChatComponentText(getCommandUsage(sender)));
                }
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (args.length < 2) {
                    Region.deleteRegion(Region.getRegionAt(player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ).getUUID());
                    player.addChatComponentMessage(new ChatComponentText("Region deleted."));
                } else if (args[1].equalsIgnoreCase("-s")) {
                    SubRegion.deleteSubRegion(SubRegion.getSubRegionAt(player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ).getUUID());
                    player.addChatComponentMessage(new ChatComponentText("SubRegion deleted."));
                } else {
                    player.addChatComponentMessage(new ChatComponentText(getCommandUsage(sender)));
                }
            } else {
                player.addChatComponentMessage(new ChatComponentText(getCommandUsage(sender)));
            }
        } catch (Exception e) {
            player.addChatComponentMessage(new ChatComponentText("ERROR: " + e.getMessage()));
            Minelife.getLogger().log(Level.WARNING, "", e);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        // TODO: Implement permissions
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

}
