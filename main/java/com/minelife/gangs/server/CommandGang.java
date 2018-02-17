package com.minelife.gangs.server;

import com.google.common.collect.Maps;
import com.minelife.ICommandHandler;
import com.minelife.Minelife;
import com.minelife.gangs.ModGangs;
import com.minelife.gangs.network.PacketOpenGangGui;
import com.minelife.gangs.server.commands.*;
import com.minelife.util.server.MLCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CommandGang extends MLCommand {

    private static Map<String, ICommandHandler> commandMap = Maps.newHashMap();

    static {
        commandMap.put("create", new Create());
        commandMap.put("home", new Home());
        commandMap.put("sethome", new SetHome());
        commandMap.put("unsethome", new UnsetHome());
        commandMap.put("help", new Help());
    }

    @Override
    public String getCommandName() {
        return "gang";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("gangs", "g");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayerMP;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return args.length > 0 && commandMap.containsKey(args[0].toLowerCase()) ? commandMap.get(args[0].toLowerCase()).isUsernameIndex(index) : false;
    }

    @Override
    public synchronized void execute(ICommandSender sender, String[] args) throws Exception {
        if(args.length == 0) {
            if(sender instanceof EntityPlayerMP) {
                if(ModGangs.getPlayerGang(((EntityPlayerMP) sender).getUniqueID()) != null) {
                    Minelife.NETWORK.sendTo(new PacketOpenGangGui(ModGangs.getPlayerGang(((EntityPlayerMP) sender).getUniqueID())), (EntityPlayerMP) sender);
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Type '/g create <name>' to create a gang."));
                }
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Only players may perform this command."));
            }
            return;
        }

        if(commandMap.containsKey(args[0].toLowerCase())) {
            commandMap.get(args[0].toLowerCase()).execute(sender, args);
        } else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Type /g help for a list of commands."));
        }
    }
}
