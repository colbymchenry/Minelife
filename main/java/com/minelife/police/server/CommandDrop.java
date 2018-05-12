package com.minelife.police.server;

import com.minelife.Minelife;
import com.minelife.util.client.PacketDropEntity;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandDrop extends CommandBase {

    @Override
    public String getName() {
        return "drop";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/drop";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = (EntityPlayer) sender;

        if(player.getPassengers().isEmpty()) {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "You aren't carrying anyone."));
            return;
        }

        Minelife.getNetwork().sendToAll(new PacketDropEntity(player.getPassengers().get(0).getEntityId()));
        player.removePassengers();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
