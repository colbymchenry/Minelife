package com.minelife.essentials.server.commands;

import com.minelife.permission.ModPermission;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        ((EntityPlayerMP) sender).setHealth(((EntityPlayerMP) sender).getMaxHealth());
        ((EntityPlayerMP) sender).getFoodStats().setFoodLevel(20);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "heal");
    }


}
