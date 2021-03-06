package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.permission.ModPermission;
import com.minelife.util.PlayerHelper;
import com.minelife.util.server.MLCommand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.List;

public class Heal extends CommandBase {

    @Override
    public String getName() {
        return "heal";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        sender.sendMessage(new TextComponentString("/heal <player>"));
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(args.length == 1) {
            if(PlayerHelper.getPlayer(args[0]) != null) {
                healPlayer(PlayerHelper.getPlayer(args[0]));
            }
        } else if(args.length < 1 && sender instanceof EntityPlayerMP) {
            healPlayer((EntityPlayerMP) sender);
        } else {
            getUsage(sender);
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "heal");
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return isUsernameIndex(args, args.length) ? CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Lists.newArrayList();
    }

    public static void healPlayer(EntityPlayerMP player) {
        player.setHealth(player.getMaxHealth());
        player.getFoodStats().setFoodLevel(20);
    }
}
