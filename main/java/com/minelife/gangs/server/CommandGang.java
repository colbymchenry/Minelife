package com.minelife.gangs.server;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.gangs.ModGangs;
import com.minelife.gangs.network.PacketOpenGangGui;
import com.minelife.gangs.server.commands.Create;
import com.minelife.gangs.server.commands.Home;
import com.minelife.gangs.server.commands.SetHome;
import com.minelife.gangs.server.commands.UnsetHome;
import com.minelife.util.server.MLCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

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
                    sender.addChatMessage(new ChatComponentText("Unknown sub-command. Type /g help for a list of commands."));
                }
            } else {
                sender.addChatMessage(new ChatComponentText("Unknown sub-command. Type /g help for a list of commands."));
            }
            return;
        }

        if(commandMap.containsKey(args[0].toLowerCase())) {
            commandMap.get(args[0].toLowerCase()).execute(sender, args);
        } else {
            getCommandUsage(sender);
        }
    }
}
