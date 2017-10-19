package com.minelife.region.server;

import com.google.common.collect.Lists;
import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.permission.ModPermission;
import com.minelife.util.WorldEditHook;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

import java.util.List;
import java.util.logging.Level;

public class CommandRegion implements ICommand {

    @Override
    public String getCommandName()
    {
        return "region";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        sender.addChatMessage(new ChatComponentText("/region create"));
        sender.addChatMessage(new ChatComponentText("/region delete"));
        return null;
    }

    @Override
    public List getCommandAliases()
    {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length != 1) {
            getCommandUsage(sender);
            return;
        }

        com.sk89q.worldedit.regions.Region weRegion = WorldEditHook.getSelection((EntityPlayerMP) sender);

        EntityPlayerMP player = (EntityPlayerMP) sender;
        String worldName = player.getEntityWorld().getWorldInfo().getWorldName();

        try {
            if (args[0].equalsIgnoreCase("create")) {
                if (weRegion == null || weRegion.getMinimumPoint() == null || weRegion.getMaximumPoint() == null)
                    throw new CustomMessageException("Incomplete WorldEdit estateselection.");
                Region.create(worldName, AxisAlignedBB.getBoundingBox(
                        weRegion.getMinimumPoint().getBlockX(), weRegion.getMinimumPoint().getBlockY(), weRegion.getMinimumPoint().getBlockZ(),
                        weRegion.getMaximumPoint().getBlockX(), weRegion.getMaximumPoint().getBlockY(), weRegion.getMaximumPoint().getBlockZ()));
                player.addChatComponentMessage(new ChatComponentText("Region created!"));
            } else if (args[0].equalsIgnoreCase("delete")) {
                Region insideOfRegion = Region.getRegionAt(worldName, Vec3.createVectorHelper(player.posX, player.posY, player.posZ));
                if(insideOfRegion == null) throw new CustomMessageException("There is no region here.");
                Region.delete(insideOfRegion.getUniqueID());
                player.addChatComponentMessage(new ChatComponentText("Region deleted."));
            } else {
                getCommandUsage(sender);
            }
        } catch (Exception e) {
            if (e instanceof CustomMessageException) {
                player.addChatComponentMessage(new ChatComponentText(e.getMessage()));
            } else {
                e.printStackTrace();
                Minelife.getLogger().log(Level.SEVERE, "", e);
                player.addChatComponentMessage(new ChatComponentText(Minelife.default_error_message));
            }
        }

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        // TODO
//        return sender instanceof EntityPlayerMP && ModPermission.get(((EntityPlayerMP) sender).getUniqueID()).hasPermission("region");
            return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return false;
    }

    @Override
    public int compareTo(Object o)
    {
        return 0;
    }
}
