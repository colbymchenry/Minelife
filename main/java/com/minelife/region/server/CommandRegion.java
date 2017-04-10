package com.minelife.region.server;

import com.google.common.collect.Lists;
import com.minelife.WorldEditHook;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.sql.SQLException;
import java.util.List;

public class CommandRegion implements ICommand {

    @Override
    public String getCommandName() {
        return "region";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/region create\n/region delete";
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

        if (args[0].equalsIgnoreCase("create")) {
            com.sk89q.worldedit.regions.Region selection = WorldEditHook.getSelection(player);

            if (selection == null || selection.getMinimumPoint() == null || selection.getMaximumPoint() == null) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Error: No valid selection was made."));
                return;
            }

            int[] min = new int[]{selection.getMinimumPoint().getBlockX(), selection.getMinimumPoint().getBlockY(), selection.getMinimumPoint().getBlockZ()};
            int[] max = new int[]{selection.getMaximumPoint().getBlockX(), selection.getMaximumPoint().getBlockY(), selection.getMaximumPoint().getBlockZ()};

            try {
                Region.createRegion(sender.getEntityWorld().getWorldInfo().getWorldName(), min, max);
            } catch (Exception e) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Error: " + e.getMessage()));
            }
        } else if (args[0].equalsIgnoreCase("delete")) {
            try {
                Region.deleteRegion(Region.getRegionAt(player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ).getUUID());
            } catch (SQLException e) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Error: " + e.getMessage()));
            }
        } else {
            player.addChatComponentMessage(new ChatComponentText(getCommandUsage(sender)));
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
