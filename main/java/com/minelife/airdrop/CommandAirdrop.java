package com.minelife.airdrop;

import com.minelife.util.PlayerHelper;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;
import java.util.UUID;

public class CommandAirdrop extends CommandBase {

    @Override
    public String getName() {
        return "airdrop";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/airdrop drop\n/airdrop reload";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length != 1) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        if(args[0].equalsIgnoreCase("drop")) {
            Airdrop airdrop = new Airdrop();
            airdrop.x = sender.getPosition().getX();
            airdrop.y = 249;
            airdrop.z = sender.getPosition().getZ();
            airdrop.id = UUID.randomUUID();
            airdrop.world = sender.getEntityWorld();
            airdrop.initLoot();
            airdrop.spawnBandits();
            ModAirdrop.airdrops.add(airdrop);
            airdrop.sendToAll();
            PlayerHelper.sendMessageToAll("&4&lAirDrop dropped!");
        } else if(args[0].equalsIgnoreCase("reload")) {
            try {
                ModAirdrop.config.reload();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
        }
    }
}
